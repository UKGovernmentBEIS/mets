package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRQuarter;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRequestIdGenerator;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WasteQDRCreationValidationServiceTest {

    @InjectMocks
    private WasteQDRCreationValidationService service;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private WasteQDRRequestIdGenerator wasteQDRRequestIdGenerator;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validateYearQuarter_valid() {
        Long accountId = 1L;
        Year year = Year.of(2025);
        WasteQDRQuarter quarter = WasteQDRQuarter.Q1;
        String requestId = "WASTE-QDR-001-2025-Q1";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(WasteQDRRequestMetaData.builder()
                        .type(RequestMetadataType.WASTE_QDR)
                        .year(year)
                        .quarter(quarter)
                        .build())
                .build();

        when(wasteQDRRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);

        RequestCreateValidationResult result = service.validateYearQuarter(accountId, year, quarter);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getReportedRequestTypes()).isEmpty();

        verify(wasteQDRRequestIdGenerator).generate(params);
        verify(requestQueryService).existsRequestById(requestId);
    }

    @Test
    void validateYearQuarter_qdrExists_returnsInvalid() {
        Long accountId = 1L;
        Year year = Year.of(2025);
        WasteQDRQuarter quarter = WasteQDRQuarter.Q2;
        String requestId = "WASTE-QDR-001-2025-Q2";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(WasteQDRRequestMetaData.builder()
                        .type(RequestMetadataType.WASTE_QDR)
                        .year(year)
                        .quarter(quarter)
                        .build())
                .build();

        when(wasteQDRRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        RequestCreateValidationResult result = service.validateYearQuarter(accountId, year, quarter);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getReportedRequestTypes()).containsExactly(RequestType.WASTE_QDR);

        verify(wasteQDRRequestIdGenerator).generate(params);
        verify(requestQueryService).existsRequestById(requestId);
    }

    @Test
    void validateAccountStatus_valid() {
        Long accountId = 1L;
        Set<AccountStatus> applicableStatuses = Set.of(InstallationAccountStatus.LIVE);

        when(requestCreateValidatorService.validate(accountId, applicableStatuses, Set.of()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        RequestCreateValidationResult result = service.validateAccountStatus(accountId);

        assertThat(result.isValid()).isTrue();
        verify(requestCreateValidatorService).validate(accountId, applicableStatuses, Set.of());
    }

    @Test
    void validateAccountStatus_invalid() {
        Long accountId = 1L;
        Set<AccountStatus> applicableStatuses = Set.of(InstallationAccountStatus.LIVE);

        when(requestCreateValidatorService.validate(accountId, applicableStatuses, Set.of()))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        RequestCreateValidationResult result = service.validateAccountStatus(accountId);

        assertThat(result.isValid()).isFalse();
        verify(requestCreateValidatorService).validate(accountId, applicableStatuses, Set.of());
    }

    @Test
    void validateAccountEmitterType_validWaste() {
        Long accountId = 1L;
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .emitterType(EmitterType.WASTE)
                .status(InstallationAccountStatus.LIVE)
                .build();

        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        RequestCreateValidationResult result = service.validateAccountEmitterType(accountId);

        assertThat(result.isValid()).isTrue();
        verify(installationAccountQueryService).getAccountDTOById(accountId);
    }

    @Test
    void validateAccountEmitterType_notWaste_returnsInvalid() {
        Long accountId = 1L;
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(accountId)
                .emitterType(EmitterType.GHGE) // not WASTE
                .status(InstallationAccountStatus.LIVE)
                .build();

        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        RequestCreateValidationResult result = service.validateAccountEmitterType(accountId);

        assertThat(result.isValid()).isFalse();
        verify(installationAccountQueryService).getAccountDTOById(accountId);
    }
}


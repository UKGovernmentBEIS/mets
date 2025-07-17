package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRRequestIdGenerator;

import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;



@ExtendWith(MockitoExtension.class)
public class ALRCreationValidationServiceTest {

    @InjectMocks
    private ALRCreationValidationService service;

    @Mock
    private RequestCreateValidatorService requestCreateValidatorService;

    @Mock
    private ALRRequestIdGenerator alrRequestIdGenerator;

    @Mock
    private RequestQueryService requestQueryService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Test
    void validateYear() {
        Long accountId = 1L;
        String requestId = "ALR00178-2025";


        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).year(Year.of(2025)).build())
                .build();

        when(alrRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(false);


        RequestCreateValidationResult validationResult = service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isTrue();

        verify(alrRequestIdGenerator, times(1)).generate(params);
        verify(requestQueryService, times(1)).existsRequestById(requestId);
    }

    @Test
    void validateYear_alrForThatYearExists_ReturnFalseValidation() {
        Long accountId = 1L;
        String requestId = "ALR00178-2025";

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).year(Year.of(2025)).build())
                .build();

        when(alrRequestIdGenerator.generate(params)).thenReturn(requestId);
        when(requestQueryService.existsRequestById(requestId)).thenReturn(true);

        RequestCreateValidationResult validationResult = service.validateYear(accountId, Year.of(2025));

        assertThat(validationResult.isValid()).isFalse();
        assertThat(validationResult.getReportedRequestTypes()).containsExactlyInAnyOrder(RequestType.ALR);

        verify(alrRequestIdGenerator, times(1)).generate(params);
        verify(requestQueryService, times(1)).existsRequestById(requestId);
    }

    @Test
    void validateAccountStatus() {
        Long accountId = 1L;
        when(requestCreateValidatorService.validate(accountId,  Set.of(InstallationAccountStatus.LIVE), Set.of())).thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        RequestCreateValidationResult validationResult = service.validateAccountStatus(accountId);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void validateAccountStatus_notApplicableAccountStatus_ReturnFalseValidation() {
        Long accountId = 1L;
        when(requestCreateValidatorService.validate(accountId, Set.of(InstallationAccountStatus.LIVE), Set.of())).thenReturn(RequestCreateValidationResult.builder().valid(false).build());
        RequestCreateValidationResult validationResult = service.validateAccountStatus(accountId);
        assertThat(validationResult.isValid()).isFalse();
    }

    @Test
    void validateEmitterTypeAndFA() {
        Long accountId = 1L;
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .status(InstallationAccountStatus.LIVE)
                .id(accountId)
                .emitterType(EmitterType.GHGE)
                .faStatus(true)
                .build();
        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);
        RequestCreateValidationResult validationResult = service.validateAccountEmitterTypeAndFreeAllocations(accountId);
        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void validateEmitterTypeAndFA_invalidInput_returnFalseValidation() {
        Long accountId = 1L;
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .status(InstallationAccountStatus.LIVE)
                .id(accountId)
                .emitterType(EmitterType.HSE)
                .faStatus(true)
                .build();

        when(installationAccountQueryService.getAccountDTOById(accountId)).thenReturn(accountDTO);

        RequestCreateValidationResult validationResult = service.validateAccountEmitterTypeAndFreeAllocations(accountId);
        assertThat(validationResult.isValid()).isFalse();

        accountDTO.setEmitterType(EmitterType.GHGE);
        accountDTO.setFaStatus(false);

        RequestCreateValidationResult validationResult1 = service.validateAccountEmitterTypeAndFreeAllocations(accountId);
        assertThat(validationResult1.isValid()).isFalse();

    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRQuarter;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation.WasteQDRCreationValidationService;

import java.time.LocalDate;
import java.time.Year;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class WasteQDRCreationServiceTest {
    @InjectMocks
    private WasteQDRCreationService service;

    @Mock
    private WasteQDRCreationValidationService wasteQDRCreationValidationService;

    @Mock
    private WasteQDRDueDateService wasteQDRDueDateService;

    @Mock
    private DateService dateService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createWasteQDR_success_Q3() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Year currentYear = Year.of(2025);

        when(dateService.getYear()).thenReturn(currentYear);
        when(dateService.getLocalDate()).thenReturn(LocalDate.of(2025, 10, 10));
        when(wasteQDRDueDateService.generateDueDate()).thenReturn(expirationDate);
        when(wasteQDRCreationValidationService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(wasteQDRCreationValidationService.validateYearQuarter(accountId, currentYear, WasteQDRQuarter.Q3))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(wasteQDRCreationValidationService.validateAccountEmitterType(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        Map<String, Object> processVars = Map.of(BpmnProcessConstants.WASTE_QDR_EXPIRATION_DATE, expirationDate);

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.WASTE_QDR)
                .accountId(accountId)
                .requestPayload(WasteQDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.WASTE_QDR_REQUEST_PAYLOAD)
                        .qdr(WasteQDR.builder().build())
                        .build())
                .requestMetadata(WasteQDRRequestMetaData.builder()
                        .type(RequestMetadataType.WASTE_QDR)
                        .year(currentYear)
                        .quarter(WasteQDRQuarter.Q3)
                        .build())
                .processVars(processVars)
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.WASTE_QDR)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(startProcessRequestService.startProcess(requestParams)).thenReturn(request);

        Request actual = service.createWasteQDR(accountId);

        assertThat(actual).isEqualTo(request);

    }

    @Test
    void createWasteQDR_accountStatusInvalid_throws() {
        Long accountId = 1L;
        when(wasteQDRCreationValidationService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());
        when(dateService.getLocalDate()).thenReturn(LocalDate.of(2025, 10, 10));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createWasteQDR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createWasteQDR_yearQuarterInvalid_throws() {
        Long accountId = 1L;
        Year year = Year.of(2025);
        Date expirationDate = new Date();

        when(dateService.getYear()).thenReturn(year);
        when(dateService.getLocalDate()).thenReturn(LocalDate.of(2025, 7, 1)); // Q2
        when(wasteQDRDueDateService.generateDueDate()).thenReturn(expirationDate);
        when(wasteQDRCreationValidationService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(wasteQDRCreationValidationService.validateYearQuarter(accountId, year, WasteQDRQuarter.Q2))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createWasteQDR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createWasteQDR_emitterTypeInvalid_throws() {
        Long accountId = 1L;
        Year year = Year.of(2025);
        Date expirationDate = new Date();

        when(dateService.getYear()).thenReturn(year);
        when(dateService.getLocalDate()).thenReturn(LocalDate.of(2025, 7, 1)); // Q2
        when(wasteQDRDueDateService.generateDueDate()).thenReturn(expirationDate);
        when(wasteQDRCreationValidationService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(wasteQDRCreationValidationService.validateYearQuarter(accountId, year, WasteQDRQuarter.Q2))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());
        when(wasteQDRCreationValidationService.validateAccountEmitterType(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createWasteQDR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.WASTE_QDR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }
}

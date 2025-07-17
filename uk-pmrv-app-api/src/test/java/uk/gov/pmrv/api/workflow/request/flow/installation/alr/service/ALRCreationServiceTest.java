package uk.gov.pmrv.api.workflow.request.flow.installation.alr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALR;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation.ALRCreationValidationService;

import java.time.Year;
import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class ALRCreationServiceTest {

    @InjectMocks
    private ALRCreationService service;

    @Mock
    private ALRCreationValidationService alrCreationValidatorService;

    @Mock
    private ALRDueDateService alrDueDateService;

    @Mock
    private DateService dateService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createALR() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Map<String, Object> processVars = Map.of(
                BpmnProcessConstants.ALR_EXPIRATION_DATE, expirationDate);

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.ALR)
                .accountId(accountId)
                .requestPayload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(ALR.builder().build())
                        .build())
                .requestMetadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .year(Year.of(2025))
                        .build())
                .processVars(processVars)
                .build();

        Request request = Request
                .builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.ALR)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(dateService.getYear())
                .thenReturn(Year.of(2025));

        when(alrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrCreationValidatorService.validateAccountEmitterTypeAndFreeAllocations(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrDueDateService.generateDueDate())
                .thenReturn(expirationDate);

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(request);

        Request actualRequest = service.createALR(accountId);

        assertThat(actualRequest).isEqualTo(request);

        verify(alrCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(alrCreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(alrDueDateService, times(1)).generateDueDate();

        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }

    @Test
    void createAlr_accountStatusIsNotValid_throwALR_CREATION_NOT_ALLOWEDException() {
        Long accountId = 1L;

        when(alrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createALR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.ALR_CREATION_NOT_ALLOWED);

        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createAlr_yearIsNotValid_throwALR_CREATION_NOT_ALLOWEDException() {
        Long accountId = 1L;

        when(dateService.getYear())
                .thenReturn(Year.of(2025));

        when(alrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrCreationValidatorService.validateYear(accountId, Year.of(2025)))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createALR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.ALR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createAlr_emitterTypeAndFreeAllocationsIsNotValid_throwALR_CREATION_NOT_ALLOWEDException() {
        Long accountId = 1L;

        when(dateService.getYear())
                .thenReturn(Year.of(2025));

        when(alrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrCreationValidatorService.validateYear(accountId, Year.of(2025)))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(alrCreationValidatorService.validateAccountEmitterTypeAndFreeAllocations(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createALR(accountId));

        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.ALR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createALRForYear() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Year alrYear = Year.of(2025);
        Map<String, Object> processVars = Map.of(
                BpmnProcessConstants.ALR_EXPIRATION_DATE, expirationDate);

        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.ALR)
                .accountId(accountId)
                .requestPayload(ALRRequestPayload.builder()
                        .payloadType(RequestPayloadType.ALR_REQUEST_PAYLOAD)
                        .alr(ALR.builder().build())
                        .build())
                .requestMetadata(ALRRequestMetaData.builder()
                        .type(RequestMetadataType.ALR)
                        .year(Year.of(2025))
                        .build())
                .processVars(processVars)
                .build();

        Request request = Request.builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.ALR)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(alrCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(request);

        when(alrCreationValidatorService.validateAccountEmitterTypeAndFreeAllocations(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        Request actualRequest = service.createALRForYear(accountId, alrYear, expirationDate);

        assertThat(actualRequest).isEqualTo(request);

        verify(alrCreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

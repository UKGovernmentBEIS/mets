package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2InitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2RequestPayload;

import java.time.Year;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2CreationServiceTest {

    @InjectMocks
    private BDRS2CreationService service;

    @Mock
    private BDRS2CreationValidationService bdrs2CreationValidatorService;

    @Mock
    private BDRS2DueDateService bdrs2DueDateService;

    @Mock
    private DateService dateService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createBDRS2() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Map<String, Object> processVars = Map.of(BpmnProcessConstants.BDRS2_EXPIRATION_DATE, expirationDate);


        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .requestPayload(BDRS2RequestPayload.builder()
                        .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                        .bdrs2(BDRS2.builder().build())
                        .build())
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .bdrs2InitiationType(BDRS2InitiationType.INITIATED)
                        .year(Year.of(2025))
                        .build())
                .processVars(processVars)
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(dateService.getYear())
                .thenReturn(Year.of(2025));

        when(bdrs2CreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrs2CreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrs2DueDateService.generateDueDate())
                .thenReturn(expirationDate);

        when(startProcessRequestService.startProcess(any(RequestParams.class)))
                .thenReturn(request);



        Request actualRequest = service.createBDRS2(accountId);


        assertThat(actualRequest).isEqualTo(request);

        verify(bdrs2CreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(bdrs2CreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(bdrs2DueDateService, times(1)).generateDueDate();

        verify(startProcessRequestService, times(1)).startProcess(any(RequestParams.class));
    }


    @Test
    void createBdrs2_accountStatusIsNotValid_throwBDRS2_CREATION_NOT_ALLOWEDException(){
        Long accountId = 1L;

        when(bdrs2CreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createBDRS2(accountId));


        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.BDRS2_CREATION_NOT_ALLOWED);

        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createBdrs2_yearIsNotValid_throwBDRS2_CREATION_NOT_ALLOWEDException(){
        Long accountId = 1L;

        when(dateService.getYear())
                .thenReturn(Year.of(2025));


        when(bdrs2CreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());


        when(bdrs2CreationValidatorService.validateYear(accountId,Year.of(2025)))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createBDRS2(accountId));


        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.BDRS2_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createBDRS2_setsInitiationTypeProcessVar() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Year bdrYear = Year.of(2025);
        Map<String, Object> processVars = Map.of(
            BpmnProcessConstants.BDRS2_EXPIRATION_DATE, expirationDate,
            BpmnProcessConstants.BDRS2_INITIATION_TYPE, BDRS2InitiationType.INITIATED
        );


        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .requestPayload(BDRS2RequestPayload.builder()
                        .payloadType(RequestPayloadType.BDRS2_REQUEST_PAYLOAD)
                        .bdrs2(BDRS2.builder().build())
                        .build())
                .requestMetadata(BDRS2RequestMetadata.builder()
                        .type(RequestMetadataType.BDRS2)
                        .bdrs2InitiationType(BDRS2InitiationType.INITIATED)
                        .year(Year.of(2025))
                        .build())
                .processVars(processVars)
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.BDRS2)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(dateService.getYear())
            .thenReturn(bdrYear);

        when(bdrs2CreationValidatorService.validateAccountStatus(accountId))
            .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrs2CreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrs2DueDateService.generateDueDate())
            .thenReturn(expirationDate);

        when(startProcessRequestService.startProcess(any(RequestParams.class)))
                .thenReturn(request);

        Request actualRequest = service.createBDRS2(accountId);


        assertThat(actualRequest).isEqualTo(request);

        ArgumentCaptor<RequestParams> requestParamsCaptor = ArgumentCaptor.forClass(RequestParams.class);
        verify(startProcessRequestService, times(1)).startProcess(requestParamsCaptor.capture());
        assertThat(requestParamsCaptor.getValue().getProcessVars()).isEqualTo(processVars);

        verify(bdrs2CreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(bdrs2CreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(bdrs2DueDateService, times(1)).generateDueDate();
    }
}
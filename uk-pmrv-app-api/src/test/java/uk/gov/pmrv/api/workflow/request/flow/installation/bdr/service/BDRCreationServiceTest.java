package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDR;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRInitiationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestPayload;

import java.time.Year;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BDRCreationServiceTest {

    @InjectMocks
    private BDRCreationService service;

    @Mock
    private BDRCreationValidationService bdrCreationValidatorService;

    @Mock
    private BDRDueDateService bdrDueDateService;

    @Mock
    private DateService dateService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Test
    void createBDR() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Map<String, Object> processVars = Map.of(
                BpmnProcessConstants.BDR_EXPIRATION_DATE, expirationDate,
                BpmnProcessConstants.BDR_INITIATION_TYPE, BDRInitiationType.INITIATED);


        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDR)
                .accountId(accountId)
                .requestPayload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .bdr(BDR.builder().build())
                        .build())
                .requestMetadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .year(Year.of(2025))
                        .bdrInitiationType(BDRInitiationType.INITIATED)
                        .build())
                .processVars(processVars)
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.BDR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(dateService.getYear())
                .thenReturn(Year.of(2025));

        when(bdrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(bdrDueDateService.generateDueDate())
                .thenReturn(expirationDate);

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(request);



        Request actualRequest = service.createBDR(accountId);


        assertThat(actualRequest).isEqualTo(request);

        verify(bdrCreationValidatorService, times(1)).validateAccountStatus(accountId);
        verify(bdrCreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(bdrDueDateService, times(1)).generateDueDate();

        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }


    @Test
    void createBdr_accountStatusIsNotValid_throwBDR_CREATION_NOT_ALLOWEDException(){
        Long accountId = 1L;

        when(bdrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createBDR(accountId));


        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.BDR_CREATION_NOT_ALLOWED);

        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createBdr_yearIsNotValid_throwBDR_CREATION_NOT_ALLOWEDException(){
        Long accountId = 1L;

        when(dateService.getYear())
                .thenReturn(Year.of(2025));


        when(bdrCreationValidatorService.validateAccountStatus(accountId))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());


        when(bdrCreationValidatorService.validateYear(accountId,Year.of(2025)))
                .thenReturn(RequestCreateValidationResult.builder().valid(false).build());

        BusinessException ex = assertThrows(BusinessException.class, () ->
                service.createBDR(accountId));


        assertThat(ex.getErrorCode()).isEqualTo(MetsErrorCode.BDR_CREATION_NOT_ALLOWED);
        verify(startProcessRequestService, never()).startProcess(any());
    }

    @Test
    void createBDRForYear() {
        Date expirationDate = new Date();
        Long accountId = 1L;
        Year bdrYear = Year.of(2025);
        Map<String, Object> processVars = Map.of(
                BpmnProcessConstants.BDR_EXPIRATION_DATE, expirationDate,
        BpmnProcessConstants.BDR_INITIATION_TYPE, BDRInitiationType.INITIATED);


        RequestParams requestParams = RequestParams.builder()
                .type(RequestType.BDR)
                .accountId(accountId)
                .requestPayload(BDRRequestPayload.builder()
                        .payloadType(RequestPayloadType.BDR_REQUEST_PAYLOAD)
                        .bdr(BDR.builder().build())
                        .build())
                .requestMetadata(BDRRequestMetadata.builder()
                        .type(RequestMetadataType.BDR)
                        .year(Year.of(2025))
                        .bdrInitiationType(BDRInitiationType.INITIATED)
                        .build())
                .processVars(processVars)
                .build();


        Request request = Request
                .builder()
                .accountId(accountId)
                .metadata(requestParams.getRequestMetadata())
                .payload(requestParams.getRequestPayload())
                .type(RequestType.BDR)
                .accountId(accountId)
                .status(RequestStatus.IN_PROGRESS)
                .build();

        when(bdrCreationValidatorService.validateYear(eq(accountId), any()))
                .thenReturn(RequestCreateValidationResult.builder().valid(true).build());

        when(startProcessRequestService.startProcess(requestParams))
                .thenReturn(request);

        Request actualRequest = service.createBDRForYear(accountId, bdrYear, expirationDate);


        assertThat(actualRequest).isEqualTo(request);


        verify(bdrCreationValidatorService, times(1)).validateYear(eq(accountId), any());
        verify(startProcessRequestService, times(1)).startProcess(requestParams);
    }
}

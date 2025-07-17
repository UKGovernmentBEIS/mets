package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.utils.DateService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.AerCreationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferARequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.TransferParty;

@ExtendWith(MockitoExtension.class)
class PermitTransferAAerServiceTest {

    @InjectMocks
    private PermitTransferAAerService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private DateService dateService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AerCreationService aerCreationService;

    @Test
    void process_whenReceiverAerLiableAndAerExists_thenCreateReceiverAer() {

        final long transfererAccountId = 1L;
        final long receiverAccountId = 2L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final String aerRequestId = "aerRequestId";
        final String aerProcessInstanceId = "aerProcessInstanceId";
        final Request transferARequest = Request.builder()
            .id(requestId)
            .accountId(transfererAccountId)
            .payload(PermitTransferARequestPayload.builder()
                .relatedRequestId(relatedRequestId)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .aerLiable(TransferParty.RECEIVER)
                    .build()).build())
            .build();
        final Request transferBRequest = Request.builder().accountId(receiverAccountId).build();
        final LocalDateTime now = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
        final Request aerRequest = Request.builder()
            .id(aerRequestId)
            .type(RequestType.AER)
            .processInstanceId(aerProcessInstanceId)
            .metadata(AerRequestMetadata.builder().year(Year.of(2021)).build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(dateService.getLocalDateTime()).thenReturn(now);
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.AER), RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(aerRequest));
        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(requestService.findRequestById(relatedRequestId)).thenReturn(transferBRequest);

        service.process(requestId);

        verify(workflowService, times(1)).deleteProcessInstance(aerProcessInstanceId, "Aer workflow terminated because of permit transfer");
        assertEquals(aerRequest.getStatus(), RequestStatus.CANCELLED);
        verify(requestService, times(1)).addActionToRequest(
            aerRequest,
            null,
            RequestActionType.REQUEST_TERMINATED,
            null
        );
        verify(aerCreationService, times(1)).createRequestAer(receiverAccountId, RequestType.AER);
    }

    @Test
    void process_whenTransfererAerLiable_thenDoNothing() {

        final long transfererAccountId = 1L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final Request transferARequest = Request.builder()
            .id(requestId)
            .accountId(transfererAccountId)
            .payload(PermitTransferARequestPayload.builder()
                .relatedRequestId(relatedRequestId)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .aerLiable(TransferParty.TRANSFERER)
                    .build()).build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);

        service.process(requestId);
    }

    @Test
    void process_whenLastYearAerNotExists_thenDoNothing() {

        final long transfererAccountId = 1L;
        final String requestId = "requestId";
        final String relatedRequestId = "relatedRequestId";
        final String aerRequestId = "aerRequestId";
        final String aerProcessInstanceId = "aerProcessInstanceId";
        final Request transferARequest = Request.builder()
            .id(requestId)
            .accountId(transfererAccountId)
            .payload(PermitTransferARequestPayload.builder()
                .relatedRequestId(relatedRequestId)
                .permitTransferDetails(PermitTransferDetails.builder()
                    .aerLiable(TransferParty.RECEIVER)
                    .build()).build())
            .build();
        final LocalDateTime now = LocalDateTime.of(2022, 1, 2, 3, 4, 5);
        final Request aerRequest = Request.builder()
            .id(aerRequestId)
            .processInstanceId(aerProcessInstanceId)
            .metadata(AerRequestMetadata.builder().year(Year.of(2020)).build())
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(transferARequest);
        when(dateService.getLocalDateTime()).thenReturn(now);
        when(requestRepository.findByAccountIdAndTypeInAndStatus(transfererAccountId, List.of(RequestType.AER), RequestStatus.IN_PROGRESS))
            .thenReturn(List.of(aerRequest));

        service.process(requestId);
    }
}

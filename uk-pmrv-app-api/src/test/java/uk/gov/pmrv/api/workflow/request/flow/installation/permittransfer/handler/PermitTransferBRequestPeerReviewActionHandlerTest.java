package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation.PermitReviewRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain.PermitTransferBApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.service.PermitTransferBReviewService;

@ExtendWith(MockitoExtension.class)
class PermitTransferBRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private PermitTransferBRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private PermitReviewRequestPeerReviewValidator validator;

    @Mock
    private PermitTransferBReviewService permitTransferBReviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        
        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        final PermitTransferBApplicationReviewRequestTaskPayload requestTaskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitIssuanceDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1))
            .validate(requestTask, RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW, taskActionPayload, pmrvUser);
        verify(permitTransferBReviewService, times(1))
            .saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, pmrvUser);
        verify(requestService, times(1)).addActionToRequest(
            requestTask.getRequest(),
            null, 
            RequestActionType.PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED, 
            pmrvUser.getUserId()
        );
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED)
        );
    }

    @Test
    void process_invalid_determination() {
        
        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final String selectedPeerReviewer = "selectedPeerReviewer";
        final PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .peerReviewer(selectedPeerReviewer)
            .build();
        final PermitTransferBApplicationReviewRequestTaskPayload requestTaskPayload = PermitTransferBApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_TRANSFER_B_APPLICATION_REVIEW_PAYLOAD)
            .determination(PermitIssuanceDeemedWithdrawnDetermination.builder().reason("reason").type(DeterminationType.DEEMED_WITHDRAWN).build())
            .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").build())
            .type(RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        doThrow(new BusinessException(ErrorCode.FORM_VALIDATION))
            .when(validator)
            .validate(requestTask, RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW, taskActionPayload, pmrvUser);

        BusinessException be = assertThrows(BusinessException.class,
            () -> handler.process(
                requestTaskId,
                RequestTaskActionType.PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW,
                pmrvUser,
                taskActionPayload));

        assertEquals(ErrorCode.FORM_VALIDATION, be.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(validator, times(1))
            .validate(requestTask, RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW, taskActionPayload, pmrvUser);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.service.RequestAviationDreUkEtsApplyService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private AviationDreUkEtsRequestPeerReviewActionHandler peerReviewActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestAviationDreUkEtsApplyService aviationDreUkEtsApplyService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    @Test
    void process() {
        String requestId = "1";
        String peerReviewer = "peerReviewer";
        Long requestTaskId = 2L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW;
        AppUser appUser = AppUser.builder().userId("user").build();

        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
            .payloadType(RequestTaskActionPayloadType.AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD)
            .peerReviewer(peerReviewer)
            .build();
        Request request = Request.builder().id(requestId).build();

        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .processTaskId("processTaskId")
            .request(request)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        peerReviewActionHandler.process(requestTaskId, requestTaskActionType,  appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(peerReviewerTaskAssignmentValidator, times(1)).validate(RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW, peerReviewer, appUser);
        verify(aviationDreUkEtsApplyService, times(1)).requestPeerReview(requestTask, peerReviewer);
        verify(requestService, times(1))
            .addActionToRequest(request, null, RequestActionType.AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED, appUser.getUserId());

        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.DRE_SUBMIT_OUTCOME, AviationDreSubmitOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(peerReviewActionHandler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW);
    }
}

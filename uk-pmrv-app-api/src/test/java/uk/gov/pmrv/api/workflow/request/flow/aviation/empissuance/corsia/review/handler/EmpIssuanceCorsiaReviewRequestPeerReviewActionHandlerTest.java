package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
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
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.service.RequestEmpCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation.EmpIssuanceCorsiaReviewRequestPeerReviewValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceCorsiaReviewRequestPeerReviewActionHandlerTest {

    @InjectMocks
    private EmpIssuanceCorsiaReviewRequestPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpCorsiaReviewService requestEmpCorsiaReviewService;

    @Mock
    private RequestService requestService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private EmpIssuanceCorsiaReviewRequestPeerReviewValidatorService requestPeerReviewValidatorService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String peerReviewer = "peerReviewer";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW;
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("REQ-ID").build())
            .processTaskId("process-task-id")
            .build();
        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder().peerReviewer(peerReviewer).build();
        PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, pmrvUser, taskActionPayload);

        verify(requestPeerReviewValidatorService, times(1)).validate(requestTask,peerReviewer, pmrvUser);
        verify(requestEmpCorsiaReviewService, times(1))
            .saveRequestPeerReviewAction(requestTask,peerReviewer, pmrvUser);
        verify(requestService, times(1))
            .addActionToRequest(requestTask.getRequest(), null, RequestActionType.EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED, pmrvUser.getUserId());
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW);
    }
}
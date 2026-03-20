package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Outcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.service.BDRS2SubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation.BDRS2RequestPeerReviewValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2ApplicationPeerReviewActionHandlerTest {

    @InjectMocks
    private BDRS2ApplicationPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private BDRS2SubmitService bdrs2SubmitService;

    @Mock
    private BDRS2RequestPeerReviewValidator bdrs2RequestPeerReviewValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String requestId = "BDRS2-001";
        String processTaskId = "processTaskId";
        String userId = "userId";
        String peerReviewer = "peerReviewerId";
        AppUser appUser = AppUser.builder().userId(userId).build();

        PeerReviewRequestTaskActionPayload taskActionPayload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();

        Request request = Request.builder().id(requestId).build();
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.BDRS2_REQUEST_PEER_REVIEW, appUser, taskActionPayload);

        verify(bdrs2RequestPeerReviewValidator, times(1)).validate(requestTask, taskActionPayload, appUser);
        verify(bdrs2SubmitService, times(1)).requestPeerReview(requestTask, peerReviewer, appUser);
        verify(requestService, times(1)).addActionToRequest(
                request, null, RequestActionType.BDRS2_APPLICATION_PEER_REVIEW_REQUESTED, userId);
        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.BDRS2_REGULATOR_REVIEW_OUTCOME, BDRS2Outcome.PEER_REVIEW_REQUIRED
        ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDRS2_REQUEST_PEER_REVIEW);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NERRequestPeerReviewValidator;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERApplicationPeerReviewActionHandlerTest {

    @InjectMocks
    private NERApplicationPeerReviewActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestService requestService;

    @Mock
    private NerApplyService nerApplyService;

    @Mock
    private NERRequestPeerReviewValidator nerRequestPeerReviewValidator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String requestId = "REQ-1";
        String userId = "user";
        String peerReviewer = "peerReviewer";

        AppUser appUser = AppUser.builder()
                .userId(userId)
                .build();

        PeerReviewRequestTaskActionPayload payload = PeerReviewRequestTaskActionPayload.builder()
                .peerReviewer(peerReviewer)
                .build();

        Request request = Request.builder()
                .id(requestId)
                .build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.NER_REQUEST_PEER_REVIEW, appUser, payload);

        verify(nerRequestPeerReviewValidator)
                .validate(requestTask, payload, appUser);

        verify(nerApplyService)
                .requestPeerReview(requestTask, peerReviewer, appUser);

        verify(requestService).addActionToRequest(
                request,
                null,
                RequestActionType.NER_PEER_REVIEW_REQUESTED,
                userId
        );

        verify(workflowService).completeTask(
                processTaskId,
                Map.of(
                        BpmnProcessConstants.REQUEST_ID, requestId,
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.PEER_REVIEW_REQUIRED
                )
        );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(RequestTaskActionType.NER_REQUEST_PEER_REVIEW);
    }
}

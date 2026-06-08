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
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NERSubmitPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private NERSubmitPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_when_agree() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String userId = "peerReviewer";

        AppUser appUser = AppUser.builder()
                .userId(userId)
                .build();

        PeerReviewDecision decision = PeerReviewDecision.builder()
                .type(PeerReviewDecisionType.AGREE)
                .build();

        PeerReviewDecisionRequestTaskActionPayload payload =
                PeerReviewDecisionRequestTaskActionPayload.builder()
                        .decision(decision)
                        .build();

        Request request = Request.builder().build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_SUBMIT_PEER_REVIEW_DECISION,
                appUser,
                payload
        );

        verify(requestService).addActionToRequest(
                eq(request),
                any(PeerReviewDecisionSubmittedRequestActionPayload.class),
                eq(RequestActionType.NER_PEER_REVIEW_ACCEPTED),
                eq(userId)
        );

        verify(workflowService).completeTask(processTaskId);
    }

    @Test
    void process_when_reject() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String userId = "peerReviewer";

        AppUser appUser = AppUser.builder()
                .userId(userId)
                .build();

        PeerReviewDecision decision = PeerReviewDecision.builder()
                .type(PeerReviewDecisionType.DISAGREE)
                .build();

        PeerReviewDecisionRequestTaskActionPayload payload =
                PeerReviewDecisionRequestTaskActionPayload.builder()
                        .decision(decision)
                        .build();

        Request request = Request.builder().build();

        RequestTask requestTask = RequestTask.builder()
                .request(request)
                .processTaskId(processTaskId)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
                requestTaskId,
                RequestTaskActionType.NER_SUBMIT_PEER_REVIEW_DECISION,
                appUser,
                payload
        );

        verify(requestService).addActionToRequest(
                eq(request),
                any(PeerReviewDecisionSubmittedRequestActionPayload.class),
                eq(RequestActionType.NER_PEER_REVIEW_REJECTED),
                eq(userId)
        );

        verify(workflowService).completeTask(processTaskId);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes())
                .containsExactly(RequestTaskActionType.NER_SUBMIT_PEER_REVIEW_DECISION);
    }
}

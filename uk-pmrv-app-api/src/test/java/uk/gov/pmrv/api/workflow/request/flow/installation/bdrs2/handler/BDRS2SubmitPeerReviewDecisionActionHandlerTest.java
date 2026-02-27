package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2Outcome;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BDRS2SubmitPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private BDRS2SubmitPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String processTaskId = "processTaskId";
        String peerReviewerId = "peerReviewerId";
        AppUser appUser = AppUser.builder().userId(peerReviewerId).build();

        PeerReviewDecision decision = PeerReviewDecision.builder()
                .type(PeerReviewDecisionType.DISAGREE)
                .notes("notes")
                .build();

        PeerReviewDecisionRequestTaskActionPayload taskActionPayload =
                PeerReviewDecisionRequestTaskActionPayload.builder()
                        .decision(decision)
                        .build();

        Request request = Request.builder().id("BDRS2-001").build();
        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId(processTaskId)
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, RequestTaskActionType.BDRS2_SUBMIT_PEER_REVIEW_DECISION,
                appUser, taskActionPayload);

        ArgumentCaptor<PeerReviewDecisionSubmittedRequestActionPayload> actionPayloadCaptor =
                ArgumentCaptor.forClass(PeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                actionPayloadCaptor.capture(),
                eq(RequestActionType.BDRS2_APPLICATION_PEER_REVIEW_REJECTED),
                eq(peerReviewerId)
        );

        PeerReviewDecisionSubmittedRequestActionPayload capturedPayload = actionPayloadCaptor.getValue();
        assertThat(capturedPayload.getPayloadType())
                .isEqualTo(RequestActionPayloadType.BDRS2_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD);
        assertThat(capturedPayload.getDecision().getType()).isEqualTo(PeerReviewDecisionType.DISAGREE);

        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.BDRS2_REGULATOR_REVIEW_OUTCOME, BDRS2Outcome.SUBMITTED_TO_REGULATOR
        ));
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.BDRS2_SUBMIT_PEER_REVIEW_DECISION);
    }
}

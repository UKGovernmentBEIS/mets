package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.handler;

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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PermanentCessationSubmitPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private PermanentCessationSubmitPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        PeerReviewDecision decision = PeerReviewDecision.builder()
                .type(PeerReviewDecisionType.DISAGREE)
                .notes("not agree")
                .build();
        PeerReviewDecisionRequestTaskActionPayload payload =
                PeerReviewDecisionRequestTaskActionPayload.builder()
                        .payloadType(RequestTaskActionPayloadType.PERMANENT_CESSATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD)
                        .decision(decision)
                        .build();
        Long requestTaskId = 1L;
        String userId = "userId";
        AppUser appUser = AppUser.builder().userId(userId).build();
        String processTaskId = "processTaskId";
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
                RequestTaskActionType.PERMANENT_CESSATION_SUBMIT_PEER_REVIEW_DECISION,
                appUser,
                payload);

        ArgumentCaptor<PeerReviewDecisionSubmittedRequestActionPayload> actionPayloadArgumentCaptor =
                ArgumentCaptor.forClass(PeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
                eq(request),
                actionPayloadArgumentCaptor.capture(),
                eq(RequestActionType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REJECTED),
                eq(userId));

        PeerReviewDecisionSubmittedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getDecision()).isEqualTo(decision);
        assertThat(captorValue.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD);

        verify(workflowService, times(1)).completeTask(processTaskId);
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMANENT_CESSATION_SUBMIT_PEER_REVIEW_DECISION);
    }
}

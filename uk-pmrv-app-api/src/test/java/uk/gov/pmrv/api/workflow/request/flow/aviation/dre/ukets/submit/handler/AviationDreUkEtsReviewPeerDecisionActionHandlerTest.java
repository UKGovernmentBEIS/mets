package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain.AviationDreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

@ExtendWith(MockitoExtension.class)
class AviationDreUkEtsReviewPeerDecisionActionHandlerTest {

    @InjectMocks
    private AviationDreUkEtsReviewPeerDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        final PeerReviewDecision decision = PeerReviewDecision.builder()
            .type(PeerReviewDecisionType.DISAGREE)
            .notes("not approved")
            .build();
        final PeerReviewDecisionRequestTaskActionPayload payload =
            PeerReviewDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD)
                .decision(decision)
                .build();
        final String userId = "userId";
        final AppUser appUser = AppUser.builder().userId(userId).build();
        final String processTaskId = "processTaskId";
        final Request request = Request.builder().id("1").build();
        final RequestTask requestTask = RequestTask.builder().id(2L).request(request).processTaskId(processTaskId).build();

        when(requestTaskService.findTaskById(2L)).thenReturn(requestTask);

        handler.process(requestTask.getId(),
            RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION,
            appUser,
            payload);

        ArgumentCaptor<PeerReviewDecisionSubmittedRequestActionPayload> actionPayloadArgumentCaptor =
            ArgumentCaptor.forClass(PeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
            eq(request),
            actionPayloadArgumentCaptor.capture(),
            eq(RequestActionType.AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED),
            eq(userId));

        final PeerReviewDecisionSubmittedRequestActionPayload captorValue = actionPayloadArgumentCaptor.getValue();
        assertThat(captorValue.getDecision()).isEqualTo(decision);
        assertThat(captorValue.getPayloadType()).isEqualTo(
            RequestActionPayloadType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD);

        verify(workflowService, times(1)).completeTask(processTaskId, Map.of(
                BpmnProcessConstants.DRE_SUBMIT_OUTCOME, AviationDreSubmitOutcome.SUBMITTED
            )
        );
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.handler;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.common.domain.enumeration.AviationAerCorsia3YearPeriodOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AviationAerCorsia3YearPeriodOffsettingPeerReviewDecisionActionHandlerTest {


    @InjectMocks
    private AviationAerCorsia3YearPeriodOffsettingPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_accepted(){
        String requestId = "1";
        Long requestTaskId = 2L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION;
        AppUser appUser = AppUser.builder().userId("user").build();
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload = PeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(PeerReviewDecision.builder()
                        .type(PeerReviewDecisionType.AGREE)
                        .notes("notes")
                        .build())
                .build();

        Request request = Request.builder().id(requestId).build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1)).addActionToRequest(request, PeerReviewDecisionSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD)
                        .decision(taskActionPayload.getDecision())
                        .build(),
                RequestActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.SUBMITTED
        ));
    }

    @Test
    void process_rejected(){
        String requestId = "1";
        Long requestTaskId = 2L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION;
        AppUser appUser = AppUser.builder().userId("user").build();
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload = PeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(PeerReviewDecision.builder()
                        .type(PeerReviewDecisionType.DISAGREE)
                        .notes("notes")
                        .build())
                .build();

        Request request = Request.builder().id(requestId).build();

        RequestTask requestTask = RequestTask.builder()
                .id(requestTaskId)
                .processTaskId("processTaskId")
                .request(request)
                .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestService, times(1)).addActionToRequest(request, PeerReviewDecisionSubmittedRequestActionPayload.builder()
                        .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_SUBMITTED_PAYLOAD)
                        .decision(taskActionPayload.getDecision())
                        .build(),
                RequestActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED, appUser.getUserId());
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_OUTCOME, AviationAerCorsia3YearPeriodOffsettingSubmitOutcome.SUBMITTED
        ));
    }


    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingPeerReviewDescisionActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PeerReviewMapper;

import java.util.Map;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AviationAerCorsiaAnnualOffsettingPeerReviewDecisionActionHandlerTest {

    @InjectMocks
    private AviationAerCorsiaAnnualOffsettingPeerReviewDecisionActionHandler handler;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private PeerReviewMapper peerReviewMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void process_agreeDecision_shouldSubmitReviewAsAccepted() {
        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION;

        AppUser appUser = AppUser.builder().userId("peerReviewerUser").build();
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).build();

        PeerReviewDecision decision = PeerReviewDecision.builder().type(PeerReviewDecisionType.AGREE).build();
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload = PeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(decision).build();

        PeerReviewDecisionSubmittedRequestActionPayload actionPayload =
                PeerReviewDecisionSubmittedRequestActionPayload.builder()
                        .decision(PeerReviewDecision.builder().type(PeerReviewDecisionType.AGREE).build()).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(peerReviewMapper.toPeerReviewDecisionSubmittedRequestActionPayload(taskActionPayload,
                RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_PAYLOAD))
                .thenReturn(actionPayload);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        ArgumentCaptor<PeerReviewDecisionSubmittedRequestActionPayload> actionPayloadCaptor =
                ArgumentCaptor.forClass(PeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(eq(request),
                actionPayloadCaptor.capture(),
                eq(RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED),
                eq("peerReviewerUser"));

        PeerReviewDecisionSubmittedRequestActionPayload capturedPayload = actionPayloadCaptor.getValue();
        assert PeerReviewDecisionType.AGREE.equals(capturedPayload.getDecision().getType());

        verify(workflowService).completeTask(eq(requestTask.getProcessTaskId()), eq(Map.of(
                BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME,
                AviationAerCorsiaAnnualOffsettingSubmitOutcome.SUBMITTED
        )));
    }


    @Test
    void process_disagreeDecision_shouldSubmitReviewAsRejected() {

        Long requestTaskId = 1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION;

        AppUser appUser = AppUser.builder().userId("peerReviewerUser").build();
        Request request = Request.builder().build();
        RequestTask requestTask = RequestTask.builder().id(requestTaskId).request(request).build();

        PeerReviewDecision decision = PeerReviewDecision.builder().type(PeerReviewDecisionType.DISAGREE).build();
        PeerReviewDecisionRequestTaskActionPayload taskActionPayload = PeerReviewDecisionRequestTaskActionPayload.builder()
                .decision(decision).build();

        PeerReviewDecisionSubmittedRequestActionPayload actionPayload =
                PeerReviewDecisionSubmittedRequestActionPayload.builder()
                        .decision(PeerReviewDecision.builder().type(PeerReviewDecisionType.AGREE).build()).build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(peerReviewMapper.toPeerReviewDecisionSubmittedRequestActionPayload(taskActionPayload,
                RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_PAYLOAD))
                .thenReturn(actionPayload);

        handler.process(requestTaskId, requestTaskActionType, appUser, taskActionPayload);

        ArgumentCaptor<PeerReviewDecisionSubmittedRequestActionPayload> actionPayloadCaptor =
                ArgumentCaptor.forClass(PeerReviewDecisionSubmittedRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(eq(request),
                actionPayloadCaptor.capture(),
                eq(RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED),
                eq("peerReviewerUser"));

        PeerReviewDecisionSubmittedRequestActionPayload capturedPayload = actionPayloadCaptor.getValue();
        assert PeerReviewDecisionType.DISAGREE.equals(capturedPayload.getDecision().getType());

        verify(workflowService).completeTask(eq(requestTask.getProcessTaskId()), eq(Map.of(
                BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME,
                AviationAerCorsiaAnnualOffsettingSubmitOutcome.SUBMITTED
        )));
    }

    @Test
    void getTypes_shouldReturnSupportedActionTypes() {
        var types = handler.getTypes();
        assert(types.contains(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION));
    }

    @Test
    void getSubmitOutcomeBpmnConstantKey_shouldReturnCorrectBpmnConstant() {
        String key = handler.getSubmitOutcomeBpmnConstantKey();
        assert(key.equals(BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME));
    }
}

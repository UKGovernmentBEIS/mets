package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.handler;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.AviationAerCorsiaAnnualOffsettingRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.common.domain.enumeration.AviationAerCorsiaAnnualOffsettingSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.AbstractHandlerWithSubmitOutcomeProcessConstant;

import java.util.List;
import java.util.Map;


@Component
@AllArgsConstructor
public class AviationAerCorsiaAnnualOffsettingPeerReviewDecisionActionHandler
        extends AbstractHandlerWithSubmitOutcomeProcessConstant
        implements RequestTaskActionHandler<PeerReviewDecisionRequestTaskActionPayload> {

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        PeerReviewDecisionRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String peerReviewer = appUser.getUserId();

        AviationAerCorsiaAnnualOffsettingRequestPayload requestPayload = (AviationAerCorsiaAnnualOffsettingRequestPayload) request.getPayload();
        AviationAerCorsiaAnnualOffsettingPeerReviewDescisionActionPayload decisionPayload;
        if(requestPayload != null){
            decisionPayload = AviationAerCorsiaAnnualOffsettingPeerReviewDescisionActionPayload.builder()
                    .decision(taskActionPayload.getDecision())
                    .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_PAYLOAD)
                    .aviationAerCorsiaAnnualOffsetting(requestPayload.getAviationAerCorsiaAnnualOffsetting())
                    .aviationAerCorsiaAnnualOffsettingSectionsCompleted(requestPayload.getAviationAerCorsiaAnnualOffsettingSectionsCompleted())
                    .build();
        } else {
            decisionPayload = AviationAerCorsiaAnnualOffsettingPeerReviewDescisionActionPayload.builder()
                    .decision(taskActionPayload.getDecision())
                    .payloadType(RequestActionPayloadType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_DECISION_PAYLOAD)
                    .build();
        }


        final RequestActionType type = decisionPayload.getDecision().getType() == PeerReviewDecisionType.AGREE
                ? RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED
                : RequestActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED;
        requestService.addActionToRequest(request, decisionPayload, type, peerReviewer);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                this.getSubmitOutcomeBpmnConstantKey(), AviationAerCorsiaAnnualOffsettingSubmitOutcome.SUBMITTED
        ));
    }


    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_OUTCOME;
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_PEER_REVIEW_DECISION);
    }
}

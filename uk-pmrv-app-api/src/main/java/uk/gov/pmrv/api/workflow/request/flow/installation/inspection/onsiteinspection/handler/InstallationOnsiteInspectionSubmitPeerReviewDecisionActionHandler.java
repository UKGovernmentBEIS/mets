package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionSubmitPeerReviewDecisionActionHandler;

import java.util.List;


@Component
public class InstallationOnsiteInspectionSubmitPeerReviewDecisionActionHandler
        extends InstallationInspectionSubmitPeerReviewDecisionActionHandler {
    public InstallationOnsiteInspectionSubmitPeerReviewDecisionActionHandler(RequestService requestService, RequestTaskService requestTaskService, WorkflowService workflowService) {
        super(requestService, requestTaskService, workflowService);
    }

    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_SUBMIT_OUTCOME;
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_SUBMIT_PEER_REVIEW_DECISION);
    }

    @Override
    protected RequestActionType getAcceptedRequestActionType() {
        return RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED;
    }

    @Override
    protected RequestActionType getRejectedRequestActionType() {
        return RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_REJECTED;
    }
}

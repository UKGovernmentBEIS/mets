package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

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
public class InstallationAuditSubmitPeerReviewDecisionActionHandler
        extends InstallationInspectionSubmitPeerReviewDecisionActionHandler {
    public InstallationAuditSubmitPeerReviewDecisionActionHandler(RequestService requestService, RequestTaskService requestTaskService, WorkflowService workflowService) {
        super(requestService, requestTaskService, workflowService);
    }

    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME;
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_AUDIT_SUBMIT_PEER_REVIEW_DECISION);
    }

    @Override
    protected RequestActionType getAcceptedRequestActionType() {
        return RequestActionType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_ACCEPTED;
    }

    @Override
    protected RequestActionType getRejectedRequestActionType() {
        return RequestActionType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_REJECTED;
    }
}

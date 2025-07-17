package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation.InstallationAuditRequestPeerReviewValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionRequestPeerReviewActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;

import java.util.List;


@Component
public class InstallationAuditRequestPeerReviewActionHandler
        extends InstallationInspectionRequestPeerReviewActionHandler {

    public InstallationAuditRequestPeerReviewActionHandler(RequestTaskService requestTaskService,
                                                           InstallationAuditRequestPeerReviewValidator
                                                                   installationAuditRequestPeerReviewValidator,
                                                           RequestService requestService,
                                                           WorkflowService workflowService,
                                                           List<InstallationInspectionSubmitService>
                                                                   installationInspectionSubmitServices) {
        super(requestTaskService,
                installationAuditRequestPeerReviewValidator,
                requestService,
                workflowService,
                installationInspectionSubmitServices);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_AUDIT_REQUEST_PEER_REVIEW);
    }


    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.INSTALLATION_AUDIT_SUBMIT_OUTCOME;
    }

    @Override
    protected RequestActionType getRequestActionType() {
        return RequestActionType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_REQUESTED;
    }
}

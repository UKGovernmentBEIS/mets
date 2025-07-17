package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionRequestPeerReviewActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation.InstallationOnsiteInspectionRequestPeerReviewValidator;

import java.util.List;


@Component
public class InstallationOnsiteInspectionRequestPeerReviewActionHandler
        extends InstallationInspectionRequestPeerReviewActionHandler {

    public InstallationOnsiteInspectionRequestPeerReviewActionHandler(RequestTaskService requestTaskService,
                                                           InstallationOnsiteInspectionRequestPeerReviewValidator installationOnsiteInspectionRequestPeerReviewValidator,
                                                           RequestService requestService,
                                                           WorkflowService workflowService,
                                                           List<InstallationInspectionSubmitService>
                                                                              installationInspectionSubmitServices) {
        super(requestTaskService,
                installationOnsiteInspectionRequestPeerReviewValidator,
                requestService,
                workflowService,
                installationInspectionSubmitServices);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.INSTALLATION_ONSITE_INSPECTION_REQUEST_PEER_REVIEW);
    }


    @Override
    protected String getSubmitOutcomeBpmnConstantKey() {
        return BpmnProcessConstants.INSTALLATION_ONSITE_INSPECTION_SUBMIT_OUTCOME;
    }

    @Override
    protected RequestActionType getRequestActionType() {
        return RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW_REQUESTED;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler;


import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.enumeration.InstallationInspectionSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation.InstallationInspectionRequestPeerReviewValidator;

import java.util.List;
import java.util.Map;



@RequiredArgsConstructor
public abstract class InstallationInspectionRequestPeerReviewActionHandler
        extends AbstractHandlerWithSubmitOutcomeProcessConstant
        implements RequestTaskActionHandler<PeerReviewRequestTaskActionPayload>  {

    private final RequestTaskService requestTaskService;
    private final InstallationInspectionRequestPeerReviewValidator installationInspectionRequestPeerReviewValidator;
    private final RequestService requestService;
    private final WorkflowService workflowService;
    private final List<InstallationInspectionSubmitService> installationInspectionSubmitServices;

    @Override
    public void process(Long requestTaskId,
                        RequestTaskActionType requestTaskActionType,
                        AppUser appUser,
                        PeerReviewRequestTaskActionPayload taskActionPayload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final String userId = appUser.getUserId();
        final String peerReviewer = taskActionPayload.getPeerReviewer();

        installationInspectionRequestPeerReviewValidator.validate(requestTask, taskActionPayload, appUser);

        final InstallationInspectionSubmitService installationInspectionSubmitService =
                installationInspectionSubmitServices.stream().filter(s -> s.getRequestType().equals(request.getType()))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        installationInspectionSubmitService.requestPeerReview(requestTask, peerReviewer, appUser);
        requestService.addActionToRequest(request,
                null,
                getRequestActionType(),
                userId);

        workflowService.completeTask(requestTask.getProcessTaskId(), Map.of(
                        BpmnProcessConstants.REQUEST_ID, request.getId(),
                        this.getSubmitOutcomeBpmnConstantKey(), InstallationInspectionSubmitOutcome.PEER_REVIEW_REQUIRED
                )
        );
    }

    protected abstract RequestActionType getRequestActionType();

}

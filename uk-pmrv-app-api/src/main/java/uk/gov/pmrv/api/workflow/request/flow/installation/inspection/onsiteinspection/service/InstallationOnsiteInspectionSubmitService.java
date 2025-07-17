package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.domain.InstallationOnsiteInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.validation.InstallationOnsiteInspectionValidatorService;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionSubmitService implements InstallationInspectionSubmitService {

    private final RequestService requestService;
    private final InstallationOnsiteInspectionValidatorService installationOnsiteInspectionValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Override
    public void applySaveAction(RequestTask requestTask,
                                InstallationInspectionApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload =
                (InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        taskPayload.setInstallationInspectionSectionsCompleted(
                taskActionPayload.getInstallationInspectionSectionsCompleted());
        taskPayload.setInstallationInspection(taskActionPayload.getInstallationInspection());
    }

    @Override
    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }

    @Override
    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final InstallationOnsiteInspectionRequestPayload requestPayload =
                (InstallationOnsiteInspectionRequestPayload) requestTask.getRequest().getPayload();
        final InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final InstallationInspection installationInspection = requestTaskPayload.getInstallationInspection();
        requestPayload.setInstallationInspection(installationInspection);
        requestPayload.setInstallationInspectionSectionsCompleted(
                requestTaskPayload.getInstallationInspectionSectionsCompleted());
        requestPayload.setInspectionAttachments(requestTaskPayload.getInspectionAttachments());
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.INSTALLATION_ONSITE_INSPECTION;
    }

    @Override
    public void applySubmitNotify(RequestTask requestTask,
                                  DecisionNotification decisionNotification,
                                  AppUser appUser) {
        final InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationOnsiteInspectionApplicationSubmitRequestTaskPayload) requestTask
                .getPayload();
        final InstallationInspection installationInspection = requestTaskPayload.getInstallationInspection();

        installationOnsiteInspectionValidatorService.validateInstallationInspection(installationInspection);

        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setDecisionNotification(decisionNotification);

        requestPayload.setInstallationInspection(installationInspection);
        requestPayload.setInstallationInspectionSectionsCompleted(
                requestTaskPayload.getInstallationInspectionSectionsCompleted());
        requestPayload.setInspectionAttachments(requestTaskPayload.getInspectionAttachments());
    }
}

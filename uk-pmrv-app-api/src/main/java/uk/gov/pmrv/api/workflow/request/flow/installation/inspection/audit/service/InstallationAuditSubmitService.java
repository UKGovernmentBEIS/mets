package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.domain.InstallationAuditRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation.InstallationAuditValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspection;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSaveRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionSubmitService;

@Service
@RequiredArgsConstructor
public class InstallationAuditSubmitService implements InstallationInspectionSubmitService {

    private final RequestService requestService;
    private final InstallationAuditValidatorService installationAuditValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Override
    @Transactional
    public void applySaveAction(RequestTask requestTask,
                                InstallationInspectionApplicationSaveRequestTaskActionPayload taskActionPayload) {
        final InstallationInspectionApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationAuditApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestTaskPayload.setInstallationInspection(taskActionPayload.getInstallationInspection());
        requestTaskPayload.setInstallationInspectionSectionsCompleted(taskActionPayload
                .getInstallationInspectionSectionsCompleted());
    }

    @Override
    public void cancel(String requestId) {
        final Request request = requestService.findRequestById(requestId);

        requestService.addActionToRequest(request, null,
                RequestActionType.INSTALLATION_AUDIT_APPLICATION_CANCELLED,
                request.getPayload().getRegulatorAssignee());
    }

    @Override
    public void requestPeerReview(RequestTask requestTask, String peerReviewer, AppUser appUser) {
        final Request request = requestTask.getRequest();
        final InstallationAuditRequestPayload requestPayload =
                (InstallationAuditRequestPayload) requestTask.getRequest().getPayload();
        final InstallationAuditApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationAuditApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        requestPayload.setRegulatorPeerReviewer(peerReviewer);
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        final InstallationInspection installationInspection = requestTaskPayload.getInstallationInspection();
        requestPayload.setInstallationInspection(installationInspection);
        requestPayload
                .setInstallationInspectionSectionsCompleted(requestTaskPayload
                        .getInstallationInspectionSectionsCompleted());
        requestPayload.setInspectionAttachments(requestTaskPayload.getInspectionAttachments());
    }

    @Transactional
    public void applySubmitNotify(RequestTask requestTask,
                                  DecisionNotification decisionNotification,
                                  AppUser appUser) {
        final InstallationAuditApplicationSubmitRequestTaskPayload requestTaskPayload =
                (InstallationAuditApplicationSubmitRequestTaskPayload) requestTask
                .getPayload();
        final InstallationInspection installationInspection = requestTaskPayload.getInstallationInspection();

        installationAuditValidatorService.validateInstallationInspection(installationInspection);


        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final InstallationInspectionRequestPayload requestPayload =
                (InstallationInspectionRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setDecisionNotification(decisionNotification);

        requestPayload.setInstallationInspection(installationInspection);
        requestPayload.setInstallationInspectionSectionsCompleted(requestTaskPayload
                .getInstallationInspectionSectionsCompleted());
        requestPayload.setInspectionAttachments(requestTaskPayload.getInspectionAttachments());
    }


    @Override
    public RequestType getRequestType() {
        return RequestType.INSTALLATION_AUDIT;
    }
}

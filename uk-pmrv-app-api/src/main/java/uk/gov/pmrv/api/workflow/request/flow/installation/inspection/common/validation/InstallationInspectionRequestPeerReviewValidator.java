package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.validation;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.PeerReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.domain.InstallationInspectionApplicationSubmitRequestTaskPayload;

import java.util.Objects;


@RequiredArgsConstructor
public abstract class InstallationInspectionRequestPeerReviewValidator {

    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;
    private final InstallationInspectionValidatorService validatorService;

    public void validate(final RequestTask requestTask, final PeerReviewRequestTaskActionPayload taskActionPayload,
                         final AppUser appUser) {
        final InstallationInspectionApplicationSubmitRequestTaskPayload taskPayload =
                (InstallationInspectionApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        peerReviewerTaskAssignmentValidator
                .validate(Objects.equals(requestTask.getType(), RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT) ? RequestTaskType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW
                        : RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW, taskActionPayload.getPeerReviewer(), appUser);

        validatorService.validateInstallationInspection(taskPayload.getInstallationInspection());
    }

    protected abstract RequestType getRequestType();
}

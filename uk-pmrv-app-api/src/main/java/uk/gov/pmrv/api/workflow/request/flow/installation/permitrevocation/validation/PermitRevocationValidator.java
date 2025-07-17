package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.validation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PeerReviewerTaskAssignmentValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

@Service
@Validated
@RequiredArgsConstructor
public class PermitRevocationValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validateSubmitRequestTaskPayload(@NotNull @Valid @SuppressWarnings("unused")
                                                 final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload) {
        // default validation
    }

    public void validateWaitForAppealRequestTaskPayload(@NotNull @Valid @SuppressWarnings("unused")
                                                 final PermitRevocationWaitForAppealRequestTaskPayload taskPayload) {
        // default validation
    }


    public void validateNotifyUsers(final RequestTask requestTask,
                                    final DecisionNotification decisionNotification,
                                    final AppUser appUser) {
        
        final boolean valid = decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser);
        if (!valid) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validatePeerReviewer(final String peerReviewer,
                                     final AppUser appUser) {
        
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW,
                                                     peerReviewer, 
                                                     appUser);
    }
}

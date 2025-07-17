package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationReviewDecision;

@Service
@Validated
@RequiredArgsConstructor
public class PermitNotificationValidatorService {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final PeerReviewerTaskAssignmentValidator peerReviewerTaskAssignmentValidator;

    public void validateNotificationReviewDecision(@NotNull @Valid final PermitNotificationReviewDecision reviewDecision) {
        // Validate
    }

    public void validateNotifyUsers(final RequestTask requestTask, final DecisionNotification decisionNotification, final AppUser appUser) {
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

    public void validatePeerReviewer(final String peerReviewer, final AppUser appUser) {
        peerReviewerTaskAssignmentValidator.validate(RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW,
                peerReviewer, appUser);
    }
    
    public void validateFollowUpResponse(@NotNull @Valid @SuppressWarnings("unused")
                                         final PermitNotificationFollowUpRequestTaskPayload taskPayload) {
        // default validation
    }

    public void validateNotificationFollowUpReviewDecision(@NotNull @Valid 
                                                           final PermitNotificationFollowUpReviewDecision reviewDecision) {
        if (reviewDecision.getType() != PermitNotificationFollowUpReviewDecisionType.ACCEPTED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    } 
    
    public void validateReturnForAmends(@NotNull @Valid
                                        final PermitNotificationFollowUpReviewDecision reviewDecision) {
        if (reviewDecision.getType() != PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

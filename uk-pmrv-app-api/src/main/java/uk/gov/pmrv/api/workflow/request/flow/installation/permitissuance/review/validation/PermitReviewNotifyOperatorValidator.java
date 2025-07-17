package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;

@Service
@RequiredArgsConstructor
public class PermitReviewNotifyOperatorValidator {
    
    private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(final RequestTask requestTask,
                         final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            (PermitIssuanceApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitIssuanceDeterminateable permitDetermination = taskPayload.getDetermination();
        final DecisionNotification permitDecisionNotification = payload.getDecisionNotification();

        permitReviewDeterminationValidatorService.validateDeterminationObject(permitDetermination);
        if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(permitDetermination, taskPayload, requestTask.getRequest().getType()) ||
        		!decisionNotificationUsersValidator.areUsersValid(requestTask, permitDecisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

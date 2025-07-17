package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewNotifyOperatorValidator {
	
	private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(RequestTask requestTask,
                         PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         AppUser appUser) {
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitVariationDeterminateable determination = taskPayload.getDetermination();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        permitReviewDeterminationValidatorService.validateDeterminationObject(determination);
        
		if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload,
				requestTask.getRequest().getType())
				|| !decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
			throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}
    }

}

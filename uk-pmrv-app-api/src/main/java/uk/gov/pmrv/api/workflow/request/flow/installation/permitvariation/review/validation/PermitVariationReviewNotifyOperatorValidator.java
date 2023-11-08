package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
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
                         PmrvUser pmrvUser) {
        final PermitVariationApplicationReviewRequestTaskPayload taskPayload =
            (PermitVariationApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final PermitVariationDeterminateable determination = taskPayload.getDetermination();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        permitReviewDeterminationValidatorService.validateDeterminationObject(determination);
        
		if (!permitReviewDeterminationValidatorService.isDeterminationAndDecisionsValid(determination, taskPayload,
				requestTask.getRequest().getType())
				|| !decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)) {
			throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}
    }

}

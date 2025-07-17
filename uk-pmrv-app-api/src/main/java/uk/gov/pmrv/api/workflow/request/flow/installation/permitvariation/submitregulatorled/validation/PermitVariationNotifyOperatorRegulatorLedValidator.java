package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.Determinateable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain.PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationNotifyOperatorRegulatorLedValidator {

	private final PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(RequestTask requestTask,
                         PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload payload,
                         AppUser appUser) {
        final PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (PermitVariationApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        final Determinateable determination = taskPayload.getDetermination();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        permitReviewDeterminationValidatorService.validateDeterminationObject(determination);
        
		if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
			throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}
    }
}

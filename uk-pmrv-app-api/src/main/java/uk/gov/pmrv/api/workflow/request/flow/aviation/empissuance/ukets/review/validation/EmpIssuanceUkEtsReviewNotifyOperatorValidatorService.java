package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewNotifyOperatorValidatorService {

    private final EmpIssuanceUkEtsReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(RequestTask requestTask,
                         EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload,
                         PmrvUser pmrvUser) {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpIssuanceUkEtsApplicationReviewRequestTaskPayload) requestTask.getPayload();
        EmpIssuanceDetermination determination = reviewRequestTaskPayload.getDetermination();

        reviewDeterminationValidatorService.validateDeterminationObject(determination);

        DecisionNotification decisionNotification = taskActionPayload.getDecisionNotification();

        if (!reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determination.getType()) ||
            !decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

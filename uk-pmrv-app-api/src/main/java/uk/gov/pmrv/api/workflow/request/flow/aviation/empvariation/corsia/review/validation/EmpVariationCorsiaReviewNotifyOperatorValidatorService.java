package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewNotifyOperatorValidatorService {

	private final EmpVariationCorsiaReviewDeterminationValidatorService reviewDeterminationValidatorService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(RequestTask requestTask,
                         NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload,
                         PmrvUser pmrvUser) {
        EmpVariationCorsiaApplicationReviewRequestTaskPayload reviewRequestTaskPayload =
            (EmpVariationCorsiaApplicationReviewRequestTaskPayload) requestTask.getPayload();
        EmpVariationDetermination determination = reviewRequestTaskPayload.getDetermination();

        reviewDeterminationValidatorService.validateDeterminationObject(determination);

        DecisionNotification decisionNotification = taskActionPayload.getDecisionNotification();

        if (!reviewDeterminationValidatorService.isValid(reviewRequestTaskPayload, determination.getType()) ||
            !decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

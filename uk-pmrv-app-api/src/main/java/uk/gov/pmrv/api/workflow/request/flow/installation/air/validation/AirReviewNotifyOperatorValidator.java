package uk.gov.pmrv.api.workflow.request.flow.installation.air.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AirReviewNotifyOperatorValidator {

    private final AirReviewValidator reviewValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(final RequestTask requestTask, 
                         final NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final PmrvUser pmrvUser) {
        
        final AirApplicationReviewRequestTaskPayload taskPayload = (AirApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        // Validate review payload data
        reviewValidator.validate(taskPayload);

        // Validate action payload data
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

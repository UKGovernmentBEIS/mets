package uk.gov.pmrv.api.workflow.request.flow.common.vir.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class VirReviewNotifyOperatorValidator {

    private final VirReviewValidator virReviewValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    public void validate(final RequestTask requestTask, final NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        
        final VirReviewable taskPayload = (VirReviewable) requestTask.getPayload();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();

        // Validate review payload data
        virReviewValidator.validate(taskPayload.getRegulatorReviewResponse(), taskPayload.getOperatorImprovementResponses());

        // Validate action payload data
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, decisionNotification, appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

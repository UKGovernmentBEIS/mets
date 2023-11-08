package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class ReturnOfAllowancesSubmissionValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final ReturnOfAllowancesValidator returnOfAllowancesValidator;

    public void validate(RequestTask requestTask, NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         PmrvUser pmrvUser) {
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(),
            pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final ReturnOfAllowancesApplicationSubmitRequestTaskPayload taskPayload =
            (ReturnOfAllowancesApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        returnOfAllowancesValidator.validate(taskPayload.getReturnOfAllowances());

    }
}

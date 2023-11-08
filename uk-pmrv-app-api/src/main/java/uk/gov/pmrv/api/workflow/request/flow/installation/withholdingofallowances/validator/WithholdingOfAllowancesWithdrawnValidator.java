package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesWithdrawnValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final WithholdingOfAllowancesWithdrawalValidator withholdingOfAllowancesWithdrawalValidator;

    public void validate(RequestTask requestTask, NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         PmrvUser pmrvUser) {
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(),
            pmrvUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload taskPayload =
            (WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        withholdingOfAllowancesWithdrawalValidator.validate(taskPayload.getWithholdingWithdrawal());

    }
}

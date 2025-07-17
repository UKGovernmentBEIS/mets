package uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permanentcessation.domain.PermanentCessationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.validator.ReturnOfAllowancesValidator;

@Service
@RequiredArgsConstructor
public class PermanentCessationSubmissionValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final PermanentCessationValidator permanentCessationValidator;

    public void validate(RequestTask requestTask, NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         AppUser appUser) {
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(),
            appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        final PermanentCessationApplicationSubmitRequestTaskPayload taskPayload =
            (PermanentCessationApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        permanentCessationValidator.validate(taskPayload.getPermanentCessation());

    }
}

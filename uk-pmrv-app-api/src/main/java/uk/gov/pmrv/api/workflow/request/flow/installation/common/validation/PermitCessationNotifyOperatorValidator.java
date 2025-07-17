package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitCessationNotifyOperatorValidator {

    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    private final PermitCessationValidator permitCessationValidator;

    public void validate(RequestTask requestTask, AppUser appUser,
                          NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();

        permitCessationValidator.validate(requestTaskPayload.getCessationContainer());

        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, taskActionPayload.getDecisionNotification(), appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }

}

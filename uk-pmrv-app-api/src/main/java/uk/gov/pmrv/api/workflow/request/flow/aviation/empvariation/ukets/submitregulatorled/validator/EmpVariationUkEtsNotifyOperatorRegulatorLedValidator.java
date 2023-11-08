package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.validator;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsNotifyOperatorRegulatorLedValidator {

	private final EmpVariationRegulatorLedValidator empValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    public void validate(RequestTask requestTask,
                         NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         PmrvUser pmrvUser) {
        final EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationUkEtsApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        
        empValidator.validateEmp(taskPayload);
		if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), pmrvUser)) {
			throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}
    }
}

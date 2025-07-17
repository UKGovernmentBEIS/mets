package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaNotifyOperatorRegulatorLedValidator {

	private final EmpVariationCorsiaRegulatorLedValidator empValidator;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;
    
    public void validate(final RequestTask requestTask,
                         final NotifyOperatorForDecisionRequestTaskActionPayload payload,
                         final AppUser appUser) {
        
        final EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload =
            (EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload) requestTask.getPayload();
        
        empValidator.validateEmp(taskPayload);
		if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(), appUser)) {
			throw new BusinessException(ErrorCode.FORM_VALIDATION);
		}
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.common.domain.EmpVariationCorsiaDetails;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.domain.EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaNotifyOperatorRegulatorLedValidatorTest {

	@InjectMocks
    private EmpVariationCorsiaNotifyOperatorRegulatorLedValidator cut;
	
	@Mock
	private EmpVariationCorsiaRegulatorLedValidator empValidator;
	
	@Mock
	private DecisionNotificationUsersValidator decisionNotificationUsersValidator;
	
	@Test
	void validate() {
		
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = 
			EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(taskPayload)
				.build();
		
		NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = 
			NotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.decisionNotification(DecisionNotification.builder()
						.operators(Set.of("op1"))
						.build())
				.build();
		
		AppUser appUser = AppUser.builder().userId("userId").build();
		
		when(decisionNotificationUsersValidator.areUsersValid(requestTask, taskActionPayload.getDecisionNotification(),
				appUser)).thenReturn(true);
		
		cut.validate(requestTask, taskActionPayload, appUser);
		
		verify(empValidator, times(1)).validateEmp(taskPayload);
		verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask,
				taskActionPayload.getDecisionNotification(), appUser);
	}
	
	@Test
	void validate_invalid_users() {
		
		EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload taskPayload = 
			EmpVariationCorsiaApplicationSubmitRegulatorLedRequestTaskPayload.builder()
				.empVariationDetails(EmpVariationCorsiaDetails.builder()
						.reason("reason")
						.build())
				.build();
		
		RequestTask requestTask = RequestTask.builder()
				.payload(taskPayload)
				.build();
		
		NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = 
			NotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.decisionNotification(DecisionNotification.builder()
						.operators(Set.of("op1"))
						.build())
				.build();
		
		AppUser appUser = AppUser.builder().userId("userId").build();
		
		when(decisionNotificationUsersValidator.areUsersValid(requestTask, taskActionPayload.getDecisionNotification(),
				appUser)).thenReturn(false);
		
		BusinessException be = assertThrows(BusinessException.class, () -> cut.validate(requestTask, taskActionPayload, appUser));
		assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORM_VALIDATION);
		
		verify(empValidator, times(1)).validateEmp(taskPayload);
		verify(decisionNotificationUsersValidator, times(1)).areUsersValid(requestTask,
				taskActionPayload.getDecisionNotification(), appUser);
	}
}

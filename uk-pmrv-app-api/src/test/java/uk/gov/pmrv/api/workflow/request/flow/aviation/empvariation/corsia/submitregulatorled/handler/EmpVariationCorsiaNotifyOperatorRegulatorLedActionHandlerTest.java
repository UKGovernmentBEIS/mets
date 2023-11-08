package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.service.EmpVariationCorsiaSubmitRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.submitregulatorled.validator.EmpVariationCorsiaNotifyOperatorRegulatorLedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaNotifyOperatorRegulatorLedActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaNotifyOperatorRegulatorLedActionHandler cut;
	
	@Mock
    private RequestTaskService requestTaskService;
	
	@Mock
	private EmpVariationCorsiaSubmitRegulatorLedService regulatorLedService;
	
	@Mock
	private EmpVariationCorsiaNotifyOperatorRegulatorLedValidator validator;
	
	@Mock
	private WorkflowService workflowService;
	
	@Test
	void process() {
		
		Long requestTaskId = 1L;
		RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED;
		PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
		NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.decisionNotification(DecisionNotification.builder()
						.operators(Set.of("op1"))
						.build())
				.build();
		
		Request request = Request.builder().id("requestId").build();
		RequestTask requestTask = RequestTask.builder()
	            .id(requestTaskId)
	            .processTaskId("process-task-id")
	            .request(request)
	            .build();
		
		when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

		cut.process(requestTaskId, requestTaskActionType, pmrvUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(validator, times(1)).validate(requestTask, payload, pmrvUser);
		verify(regulatorLedService, times(1)).saveDecisionNotification(requestTask,
				payload.getDecisionNotification(), pmrvUser);
		verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
				Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
		            	BpmnProcessConstants.EMP_VARIATION_SUBMIT_OUTCOME, EmpVariationSubmitOutcome.SUBMITTED,
		                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR)
				);
		
		assertThat(request.getSubmissionDate()).isNotNull();
	}
	
	@Test
	void getTypes() {
		assertThat(cut.getTypes())
				.containsExactly(RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED);
	}
}

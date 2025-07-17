package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.service.PermitVariationRegulatorLedService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.validation.PermitVariationNotifyOperatorRegulatorLedValidator;

@ExtendWith(MockitoExtension.class)
class PermitVariationNotifyOperatorRegulatorLedActionHandlerTest {

	@InjectMocks
    private PermitVariationNotifyOperatorRegulatorLedActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitVariationNotifyOperatorRegulatorLedValidator validator;

    @Mock
    private PermitVariationRegulatorLedService permitVariationRegulatorLedService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Test
    void getTypes() {
    	assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED);
    }
    
    @Test
    void process() {
    	Long requestTaskId = 1L;
    	RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED;
    	AppUser appUser = AppUser.builder().build();
		PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload payload = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED_PAYLOAD)
				.decisionNotification(DecisionNotification.builder().signatory("sign").build())
				.build();
		
		Request request = Request.builder().id("2").build();
		
		RequestTask requestTask = RequestTask.builder()
	            .id(requestTaskId)
	            .processTaskId("processTaskId")
	            .request(request)
	            .build();
		
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType,  appUser, payload);
		
		assertThat(request.getSubmissionDate()).isNotNull();
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(validator, times(1)).validate(requestTask, payload, appUser);
        verify(permitVariationRegulatorLedService, times(1)).savePermitVariationDecisionNotificationRegulatorLed(requestTask, payload.getDecisionNotification(), appUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.PERMIT_VARIATION_SUBMIT_OUTCOME, PermitVariationSubmitOutcome.SUBMITTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }
}

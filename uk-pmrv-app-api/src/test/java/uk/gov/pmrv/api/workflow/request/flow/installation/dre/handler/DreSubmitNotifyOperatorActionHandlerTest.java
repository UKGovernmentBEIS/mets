package uk.gov.pmrv.api.workflow.request.flow.installation.dre.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.Dre;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReason;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFee;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreFeeDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreSubmitOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.dre.service.DreApplyService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class DreSubmitNotifyOperatorActionHandlerTest {

	@InjectMocks
    private DreSubmitNotifyOperatorActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private DreApplyService dreApplyService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Test
	void process_with_Fee() {
    	Long requestTaskId = 1L;
    	RequestTaskActionType requestTaskActionType = RequestTaskActionType.DRE_SUBMIT_NOTIFY_OPERATOR;
    	PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.payloadType(RequestTaskActionPayloadType.DRE_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
				.decisionNotification(DecisionNotification.builder()
						.signatory("signatory")
						.build())
				.build();
		Request request = Request.builder().id("2").build();
		
		UUID att1 = UUID.randomUUID();
		LocalDate dueDate = LocalDate.now().plusDays(1);
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.fee(DreFee.builder()
						.chargeOperator(true)
						.feeDetails(DreFeeDetails.builder()
								.hourlyRate(BigDecimal.TEN)
								.totalBillableHours(BigDecimal.TEN)
								.dueDate(dueDate)
								.build())
						.build())
				.build();
    	DreApplicationSubmitRequestTaskPayload taskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dre(dre)
                .dreAttachments(Map.of(att1, "atta1.pdf"))
                .sectionCompleted(true)
                .build();
    	
		RequestTask requestTask = RequestTask.builder()
	            .id(1L)
	            .processTaskId("processTaskId")
	            .payload(taskPayload)
	            .request(request)
	            .build();
		
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType,  pmrvUser, payload);
		
		assertThat(request.getSubmissionDate()).isNotNull();
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(dreApplyService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), pmrvUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
            		BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.SUBMITTED,
            		BpmnProcessConstants.DRE_IS_PAYMENT_REQUIRED, true,
            		BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, DateUtils.convertLocalDateToDate(dre.getFee().getFeeDetails().getDueDate())));
    }
    
    @Test
	void process_without_Fee() {
    	Long requestTaskId = 1L;
    	RequestTaskActionType requestTaskActionType = RequestTaskActionType.DRE_SUBMIT_NOTIFY_OPERATOR;
    	PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
		NotifyOperatorForDecisionRequestTaskActionPayload payload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.payloadType(RequestTaskActionPayloadType.DRE_SUBMIT_NOTIFY_OPERATOR_PAYLOAD)
				.decisionNotification(DecisionNotification.builder()
						.signatory("signatory")
						.build())
				.build();
		Request request = Request.builder().id("2").build();
		
		UUID att1 = UUID.randomUUID();
    	Dre dre = Dre.builder()
				.determinationReason(DreDeterminationReason.builder()
						.operatorAskedToResubmit(true)
						.regulatorComments("reg comments")
						.build())
				.fee(DreFee.builder().chargeOperator(false).build())
				.build();
    	DreApplicationSubmitRequestTaskPayload taskPayload = DreApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DRE_APPLICATION_SUBMIT_PAYLOAD)
                .dre(dre)
                .dreAttachments(Map.of(att1, "atta1.pdf"))
                .sectionCompleted(true)
                .build();
    	
		RequestTask requestTask = RequestTask.builder()
	            .id(1L)
	            .processTaskId("processTaskId")
	            .payload(taskPayload)
	            .request(request)
	            .build();
		
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType,  pmrvUser, payload);
		
		assertThat(request.getSubmissionDate()).isNotNull();
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(dreApplyService, times(1)).applySubmitNotify(requestTask, payload.getDecisionNotification(), pmrvUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
            		BpmnProcessConstants.DRE_SUBMIT_OUTCOME, DreSubmitOutcome.SUBMITTED,
            		BpmnProcessConstants.DRE_IS_PAYMENT_REQUIRED, false));
    }
    
    @Test
    void getTypes() {
    	assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.DRE_SUBMIT_NOTIFY_OPERATOR);
    }
}

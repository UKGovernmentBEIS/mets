package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

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
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service.PermitVariationReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.validation.PermitVariationReviewNotifyOperatorValidator;

@ExtendWith(MockitoExtension.class)
public class PermitVariationReviewNotifyOperatorActionHandlerTest {

	@InjectMocks
    private PermitVariationReviewNotifyOperatorActionHandler cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitVariationReviewNotifyOperatorValidator validator;

    @Mock
    private PermitVariationReviewService permitVariationReviewService;
    
    @Mock
    private WorkflowService workflowService;
    
    @Test
    void getTypes() {
    	assertThat(cut.getTypes()).containsExactly(RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
    
    @Test
    void process() {
    	Long requestTaskId = 1L;
    	RequestTaskActionType requestTaskActionType = RequestTaskActionType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION;
    	PmrvUser pmrvUser = PmrvUser.builder().build();
		PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload payload = PermitVariationNotifyOperatorForDecisionRequestTaskActionPayload.builder()
				.payloadType(RequestTaskActionPayloadType.PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
				.decisionNotification(DecisionNotification.builder().signatory("sign").build())
				.build();
		
		RequestTask requestTask = RequestTask.builder()
	            .id(requestTaskId)
	            .processTaskId("processTaskId")
	            .payload(PermitVariationApplicationReviewRequestTaskPayload.builder()
	                .determination(PermitVariationGrantDetermination.builder()
	                    .type(DeterminationType.GRANTED).activationDate(LocalDate.now().plusDays(1)).build()).build())
	            .request(Request.builder().id("2").build())
	            .build();
		
		when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);
		
		cut.process(requestTaskId, requestTaskActionType,  pmrvUser, payload);
		
		verify(requestTaskService, times(1)).findTaskById(requestTaskId);
		verify(validator, times(1)).validate(requestTask, payload, pmrvUser);
        verify(permitVariationReviewService, times(1)).savePermitVariationDecisionNotification(requestTask, payload.getDecisionNotification(), pmrvUser);
        verify(workflowService, times(1)).completeTask(requestTask.getProcessTaskId(),
            Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, DeterminationType.GRANTED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }
}

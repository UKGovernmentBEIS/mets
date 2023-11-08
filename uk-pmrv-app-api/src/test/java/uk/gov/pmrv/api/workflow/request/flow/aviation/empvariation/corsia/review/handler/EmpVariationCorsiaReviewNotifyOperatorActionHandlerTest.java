package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.handler;

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

import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.service.EmpVariationCorsiaReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation.EmpVariationCorsiaReviewNotifyOperatorValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

@ExtendWith(MockitoExtension.class)
class EmpVariationCorsiaReviewNotifyOperatorActionHandlerTest {

	@InjectMocks
    private EmpVariationCorsiaReviewNotifyOperatorActionHandler notifyOperatorActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private EmpVariationCorsiaReviewService empVariationCorsiaReviewService;

    @Mock
    private EmpVariationCorsiaReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String requestId = "REQUEST-1";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION;
        PmrvUser pmrvUser = PmrvUser.builder().build();
        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        NotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload =
        		NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        EmpVariationDetermination determination = EmpVariationDetermination.builder().type(EmpVariationDeterminationType.APPROVED).build();
        EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload =
        		EmpVariationCorsiaApplicationReviewRequestTaskPayload.builder()
                .determination(determination)
                .build();
        Request request = Request.builder().id(requestId).build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .processTaskId("process-task-id")
            .payload(requestTaskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        notifyOperatorActionHandler.process(requestTaskId, requestTaskActionType, pmrvUser, requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(empVariationCorsiaReviewService,times(1))
            .saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
        verify(reviewNotifyOperatorValidatorService, times(1))
            .validate(requestTask, requestTaskActionPayload, pmrvUser);
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.REVIEW_DETERMINATION, determination.getType(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void getTypes() {
        assertThat(notifyOperatorActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION);
    }
}

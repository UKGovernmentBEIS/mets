package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDetermination;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.service.RequestEmpUkEtsReviewService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation.EmpIssuanceUkEtsReviewNotifyOperatorValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmpIssuanceUkEtsReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private EmpIssuanceUkEtsReviewNotifyOperatorActionHandler notifyOperatorActionHandler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private RequestEmpUkEtsReviewService requestEmpUkEtsReviewService;

    @Mock
    private EmpIssuanceUkEtsReviewNotifyOperatorValidatorService reviewNotifyOperatorValidatorService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        Long requestTaskId = 1L;
        String requestId = "REQUEST-1";
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION;
        AppUser appUser = AppUser.builder().build();
        DecisionNotification decisionNotification = DecisionNotification.builder().build();
        EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload requestTaskActionPayload =
            EmpIssuanceUkEtsNotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(decisionNotification)
                .build();
        EmpIssuanceDetermination determination = EmpIssuanceDetermination.builder().type(EmpIssuanceDeterminationType.APPROVED).build();
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload =
            EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder()
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
        notifyOperatorActionHandler.process(requestTaskId, requestTaskActionType, appUser, requestTaskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(requestEmpUkEtsReviewService,times(1))
            .saveDecisionNotification(requestTask, decisionNotification, appUser);
        verify(reviewNotifyOperatorValidatorService, times(1))
            .validate(requestTask, requestTaskActionPayload, appUser);
        verify(workflowService, times(1))
            .completeTask(requestTask.getProcessTaskId(), Map.of(
                BpmnProcessConstants.REQUEST_ID, requestId,
                BpmnProcessConstants.REVIEW_DETERMINATION, determination.getType(),
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }

    @Test
    void getTypes() {
        assertThat(notifyOperatorActionHandler.getTypes())
            .containsExactly(RequestTaskActionType.EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION);
    }
}
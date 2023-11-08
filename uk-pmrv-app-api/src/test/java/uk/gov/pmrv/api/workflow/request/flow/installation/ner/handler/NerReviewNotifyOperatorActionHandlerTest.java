package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
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
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerEndedDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@ExtendWith(MockitoExtension.class)
class NerReviewNotifyOperatorActionHandlerTest {

    @InjectMocks
    private NerReviewNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private NerReviewValidator reviewValidator;

    @Mock
    private NerApplyReviewService applyReviewService;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process() {
        
        final Long requestTaskId = 1L;
        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload = NotifyOperatorForDecisionRequestTaskActionPayload.builder()
            .decisionNotification(decisionNotification)
            .build();
        final NerApplicationReviewRequestTaskPayload requestTaskPayload = NerApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NER_APPLICATION_REVIEW_PAYLOAD)
            .determination(NerEndedDetermination.builder().type(NerDeterminationType.CLOSED).build())
            .build();
        final BigDecimal paymentAmount = new BigDecimal(100);
        final RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .request(Request.builder().id("2").payload(NerRequestPayload.builder().paymentAmount(paymentAmount).build()).build())
            .type(RequestTaskType.NER_APPLICATION_REVIEW)
            .payload(requestTaskPayload)
            .processTaskId("processTaskId")
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        handler.process(
            requestTaskId,
            RequestTaskActionType.NER_NOTIFY_OPERATOR_FOR_DECISION,
            pmrvUser,
            taskActionPayload);

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(reviewValidator, times(1))
            .validateReviewTaskPayload(requestTaskPayload, paymentAmount);
        verify(reviewValidator, times(1))
            .validateReviewNotifyUsers(requestTask, decisionNotification, pmrvUser);
        verify(applyReviewService, times(1))
            .saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
        verify(workflowService, times(1)).completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, "2",
                BpmnProcessConstants.REVIEW_DETERMINATION, NerDeterminationType.CLOSED,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR,
                BpmnProcessConstants.SEND_NOTICE, true
            )
        );
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.NER_NOTIFY_OPERATOR_FOR_DECISION);
    }
}

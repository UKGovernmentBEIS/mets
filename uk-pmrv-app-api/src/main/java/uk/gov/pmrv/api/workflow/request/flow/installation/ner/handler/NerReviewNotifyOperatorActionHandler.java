package uk.gov.pmrv.api.workflow.request.flow.installation.ner.handler;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.enums.NerDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.service.NerApplyReviewService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation.NerReviewValidator;

@Component
@RequiredArgsConstructor
public class NerReviewNotifyOperatorActionHandler
    implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final NerReviewValidator validator;
    private final NerApplyReviewService applyReviewService;
    private final WorkflowService workflowService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final NotifyOperatorForDecisionRequestTaskActionPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final NerApplicationReviewRequestTaskPayload taskPayload =
            (NerApplicationReviewRequestTaskPayload) requestTask.getPayload();
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        final BigDecimal paymentAmount = requestTask.getRequest().getPayload().getPaymentAmount();

        validator.validateReviewTaskPayload(taskPayload, paymentAmount);
        validator.validateReviewNotifyUsers(requestTask, decisionNotification, pmrvUser);

        applyReviewService.saveDecisionNotification(requestTask, decisionNotification, pmrvUser);
        
        final NerDeterminationType determinationType = taskPayload.getDetermination().getType();
        workflowService.completeTask(
            requestTask.getProcessTaskId(),
            Map.of(
                BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                BpmnProcessConstants.REVIEW_DETERMINATION, determinationType,
                BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR,
                BpmnProcessConstants.SEND_NOTICE, true
            )
        );
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.NER_NOTIFY_OPERATOR_FOR_DECISION);
    }
}

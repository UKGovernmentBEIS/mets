package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.DecisionNotificationUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.PermitSurrenderReviewDeterminationHandlerService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.service.RequestPermitSurrenderReviewService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitSurrenderReviewNotifyOperatorActionHandler
        implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitSurrenderReviewDeterminationHandlerService reviewDeterminationHandlerService;
    private final RequestPermitSurrenderReviewService requestPermitSurrenderReviewService;
    private final WorkflowService workflowService;
    private final DecisionNotificationUsersValidator decisionNotificationUsersValidator;

    @Override
    public void process(final Long requestTaskId, final RequestTaskActionType requestTaskActionType,
            final AppUser appUser, final NotifyOperatorForDecisionRequestTaskActionPayload payload) {
        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        final PermitSurrenderApplicationReviewRequestTaskPayload reviewTaskPayload = (PermitSurrenderApplicationReviewRequestTaskPayload) requestTask
                .getPayload();

        // validate
        reviewDeterminationHandlerService.validateReview(reviewTaskPayload.getReviewDecision(),
                reviewTaskPayload.getReviewDetermination());
        if (!decisionNotificationUsersValidator.areUsersValid(requestTask, payload.getDecisionNotification(),
                appUser)) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
        
        // save
        final DecisionNotification decisionNotification = payload.getDecisionNotification();
        requestPermitSurrenderReviewService.saveReviewDecisionNotification(requestTask, decisionNotification,
                appUser);

        // complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REQUEST_ID, requestTask.getRequest().getId(),
                        BpmnProcessConstants.REVIEW_DETERMINATION, reviewTaskPayload.getReviewDetermination().getType(),
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION);
    }
}

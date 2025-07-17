package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationValidatorService;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PermitNotificationReviewNotifyOperatorActionHandler implements RequestTaskActionHandler<NotifyOperatorForDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;
    private final WorkflowService workflowService;
    private final PermitNotificationValidatorService permitNotificationValidatorService;

    @Override
    public void process(Long requestTaskId, RequestTaskActionType requestTaskActionType, AppUser appUser,
                        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);

        final PermitNotificationApplicationReviewRequestTaskPayload reviewTaskPayload =
                (PermitNotificationApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // Validate
        permitNotificationValidatorService.validateNotificationReviewDecision(reviewTaskPayload.getReviewDecision());
        permitNotificationValidatorService.validateNotifyUsers(requestTask, taskActionPayload.getDecisionNotification(), appUser);

        // Save payload to request
        final Request request = requestTask.getRequest();
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        requestPayload.setReviewDecision(reviewTaskPayload.getReviewDecision());
        requestPayload.setReviewDecisionNotification(taskActionPayload.getDecisionNotification());
        requestPayload.setRegulatorReviewer(appUser.getUserId());

        // Get determination type
        DeterminationType type = reviewTaskPayload.getReviewDecision().getType().toDeterminationType();

        // Complete task
        workflowService.completeTask(requestTask.getProcessTaskId(),
                Map.of(BpmnProcessConstants.REVIEW_DETERMINATION, type,
                        BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.NOTIFY_OPERATOR,
                        BpmnProcessConstants.REVIEW_DECISION_TYPE_OUTCOME, reviewTaskPayload.getReviewDecision().getType()
                        ));
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION);
    }
}

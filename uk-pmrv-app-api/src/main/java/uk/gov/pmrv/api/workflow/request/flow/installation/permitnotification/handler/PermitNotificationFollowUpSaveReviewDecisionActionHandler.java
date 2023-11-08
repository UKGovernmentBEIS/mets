package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;

@Component
@RequiredArgsConstructor
public class PermitNotificationFollowUpSaveReviewDecisionActionHandler
    implements RequestTaskActionHandler<PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload> {

    private final RequestTaskService requestTaskService;

    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final PermitNotificationFollowUpSaveReviewDecisionRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload();

        // validate
        LocalDate dueDate = null;
        if (actionPayload.getReviewDecision().getType() == PermitNotificationFollowUpReviewDecisionType.AMENDS_NEEDED) {
            dueDate = ((PermitNotificationFollowupRequiredChangesDecisionDetails) actionPayload.getReviewDecision().getDetails()).getDueDate();
        }
        if (dueDate != null && !dueDate.isAfter(taskPayload.getFollowUpResponseExpirationDate())) {
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }

        // update task payload
        taskPayload.setReviewDecision(actionPayload.getReviewDecision());
        taskPayload.setReviewSectionsCompleted(actionPayload.getReviewSectionsCompleted());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION);
    }
}

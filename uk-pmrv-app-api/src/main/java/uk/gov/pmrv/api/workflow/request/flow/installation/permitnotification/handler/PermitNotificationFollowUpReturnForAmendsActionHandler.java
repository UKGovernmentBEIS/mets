package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.RequestTaskActionEmptyPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReturnedForAmendsRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service.PermitNotificationValidatorService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermitNotificationFollowUpReturnForAmendsActionHandler implements RequestTaskActionHandler<RequestTaskActionEmptyPayload> {

    private final RequestTaskService requestTaskService;
    private final PermitNotificationValidatorService validatorService;
    private final RequestService requestService;
    private final WorkflowService workflowService;

    @Override
    @Transactional
    public void process(final Long requestTaskId,
        final RequestTaskActionType requestTaskActionType,
        final AppUser appUser,
        final RequestTaskActionEmptyPayload payload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload =
            (PermitNotificationFollowUpApplicationReviewRequestTaskPayload) requestTask.getPayload();

        validatorService.validateReturnForAmends(taskPayload.getReviewDecision());

        // update request payload
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) requestTask.getRequest().getPayload();
        requestPayload.setFollowUpReviewDecision(taskPayload.getReviewDecision());
        requestPayload.setFollowUpResponseAttachments(taskPayload.getFollowUpAttachments());
        requestPayload.setFollowUpReviewSectionsCompleted(taskPayload.getReviewSectionsCompleted());

        // create timeline action
        this.createRequestAction(requestTask.getRequest(), appUser, taskPayload);

        final Map<String, Object> variables = new HashMap<>();
        variables.put(BpmnProcessConstants.REVIEW_OUTCOME, ReviewOutcome.AMENDS_NEEDED);
        workflowService.completeTask(requestTask.getProcessTaskId(), variables);
    }

    private void createRequestAction(final Request request,
        final AppUser appUser,
        final PermitNotificationFollowUpApplicationReviewRequestTaskPayload taskPayload) {

        final PermitNotificationFollowupRequiredChangesDecisionDetails reviewDecisionDetails =
            (PermitNotificationFollowupRequiredChangesDecisionDetails) taskPayload.getReviewDecision().getDetails();
        final Set<UUID> files = reviewDecisionDetails.getRequiredChanges().stream().map(ReviewDecisionRequiredChange::getFiles).flatMap(Set::stream)
            .collect(Collectors.toSet());
        final Map<UUID, String> amendAttachments = taskPayload.getFollowUpAttachments().entrySet().stream()
            .filter(f -> files.contains(f.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        final PermitNotificationFollowUpReturnedForAmendsRequestActionPayload actionPayload =
            PermitNotificationFollowUpReturnedForAmendsRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS_PAYLOAD)
                .decisionDetails(
                    PermitNotificationFollowupRequiredChangesDecisionDetails.builder()
                        .notes(reviewDecisionDetails.getNotes())
                        .requiredChanges(reviewDecisionDetails.getRequiredChanges())
                        .dueDate(reviewDecisionDetails.getDueDate())
                        .build()
                )
                .amendAttachments(amendAttachments)
                .build();

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS,
            appUser.getUserId());
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

@Service
public class PermitNotificationFollowUpWaitForAmendsInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final Set<UUID> followUpResponseFiles = requestPayload.getFollowUpResponseFiles();
        final Set<UUID> amendFiles = ((PermitNotificationFollowupRequiredChangesDecisionDetails) requestPayload.getFollowUpReviewDecision()
            .getDetails()).getRequiredChanges().stream().map(
            ReviewDecisionRequiredChange::getFiles).flatMap(Collection::stream).collect(Collectors.toSet());
        final Map<UUID, String> followUpResponseAttachments = requestPayload.getFollowUpResponseAttachments()
            .entrySet().stream()
            .filter(e -> amendFiles.contains(e.getKey()) || followUpResponseFiles.contains(e.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return PermitNotificationFollowUpWaitForAmendsRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD)
            .followUpRequest(((PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision().getDetails()).getFollowUp().getFollowUpRequest())
            .followUpResponse(requestPayload.getFollowUpResponse())
            .followUpFiles(followUpResponseFiles)
            .reviewDecision(requestPayload.getFollowUpReviewDecision())
            .followUpResponseAttachments(followUpResponseAttachments)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS);
    }
}

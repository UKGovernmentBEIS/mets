package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import java.util.Set;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.mapper.PermitNotificationMapper;

@Service
public class PermitNotificationFollowUpApplicationAmendsSubmitInitializer implements InitializeRequestTaskHandler {

    private static final PermitNotificationMapper PERMIT_NOTIFICATION_MAPPER = Mappers.getMapper(PermitNotificationMapper.class);

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        final PermitNotificationFollowUpReviewDecision reviewDecision =
            PERMIT_NOTIFICATION_MAPPER.cloneFollowUpReviewDecisionIgnoreNotes(requestPayload.getFollowUpReviewDecision());

        PermitNotificationAcceptedDecisionDetails permitNotificationAcceptedDecisionDetails = (PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision()
            .getDetails();
        return PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
            .followUpRequest(permitNotificationAcceptedDecisionDetails.getFollowUp().getFollowUpRequest())
            .followUpResponseExpirationDate(permitNotificationAcceptedDecisionDetails.getFollowUp().getFollowUpResponseExpirationDate())
            .followUpResponse(requestPayload.getFollowUpResponse())
            .followUpFiles(requestPayload.getFollowUpResponseFiles())
            .followUpAttachments(requestPayload.getFollowUpResponseAttachments())
            .permitNotificationType(requestPayload.getPermitNotification().getType())
            .submissionDate(requestPayload.getFollowUpResponseSubmissionDate())
            .reviewDecision(reviewDecision)
            .reviewSectionsCompleted(requestPayload.getFollowUpReviewSectionsCompleted())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT);
    }
}

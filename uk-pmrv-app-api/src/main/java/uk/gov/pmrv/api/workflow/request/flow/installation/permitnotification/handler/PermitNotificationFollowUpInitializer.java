package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;

import java.time.LocalDate;
import java.util.Set;

@Service
public class PermitNotificationFollowUpInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        PermitNotificationRequestPayload payload = (PermitNotificationRequestPayload) request.getPayload();
        final FollowUp followUp = ((PermitNotificationAcceptedDecisionDetails) payload.getReviewDecision().getDetails()).getFollowUp();
        final String followUpRequest = followUp.getFollowUpRequest();
        final LocalDate followUpResponseExpirationDate = followUp.getFollowUpResponseExpirationDate();

        return PermitNotificationFollowUpRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD)
            .followUpRequest(followUpRequest)
            .followUpResponseExpirationDate(followUpResponseExpirationDate)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_FOLLOW_UP);
    }
}

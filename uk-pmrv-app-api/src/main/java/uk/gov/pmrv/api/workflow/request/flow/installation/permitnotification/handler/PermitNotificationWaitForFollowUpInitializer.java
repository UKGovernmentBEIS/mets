package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.handler;

import java.time.LocalDate;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.FollowUp;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;

@Service
public class PermitNotificationWaitForFollowUpInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        PermitNotificationRequestPayload payload = (PermitNotificationRequestPayload) request.getPayload();
        final FollowUp followUp = ((PermitNotificationAcceptedDecisionDetails) payload.getReviewDecision().getDetails()).getFollowUp();
        final String followUpRequest = followUp.getFollowUpRequest();
        final LocalDate followUpResponseExpirationDate = followUp.getFollowUpResponseExpirationDate();

        return PermitNotificationWaitForFollowUpRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD)
            .followUpRequest(followUpRequest)
            .followUpResponseExpirationDate(followUpResponseExpirationDate)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP);
    }
}

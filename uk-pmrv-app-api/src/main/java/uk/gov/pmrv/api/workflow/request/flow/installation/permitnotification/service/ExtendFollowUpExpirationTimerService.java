package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestExpirationType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskTimeManagementService;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationAcceptedDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowUpWaitForAmendsRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationFollowupRequiredChangesDecisionDetails;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain.PermitNotificationWaitForFollowUpRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class ExtendFollowUpExpirationTimerService {

    private final RequestService requestService;
    private final RequestTaskTimeManagementService requestTaskTimeManagementService;

    public void extendTimer(final String requestId, final Date expirationDate) {

        final Request request = requestService.findRequestById(requestId);
        final LocalDate dueDate = expirationDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        final List<RequestTask> requestTasks =
            requestTaskTimeManagementService.setDueDateToTasks(requestId, RequestExpirationType.FOLLOW_UP_RESPONSE, dueDate);
        this.updateTaskPayloads(dueDate, requestTasks);
        this.updateRequestPayload(request, dueDate);

        requestService.addActionToRequest(request,
            null,
            RequestActionType.PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED,
            request.getPayload().getRegulatorAssignee());
    }

    private void updateRequestPayload(final Request request, final LocalDate dueDate) {
        final PermitNotificationRequestPayload requestPayload = (PermitNotificationRequestPayload) request.getPayload();
        if (requestPayload.getFollowUpReviewDecision() != null) {
            ((PermitNotificationFollowupRequiredChangesDecisionDetails) requestPayload.getFollowUpReviewDecision().getDetails()).setDueDate(dueDate);
        } else {
            ((PermitNotificationAcceptedDecisionDetails) requestPayload.getReviewDecision().getDetails()).getFollowUp()
                .setFollowUpResponseExpirationDate(dueDate);
        }
    }

    private void updateTaskPayloads(final LocalDate dueDate, final List<RequestTask> requestTasks) {

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD))
            .forEach(p -> ((PermitNotificationWaitForFollowUpRequestTaskPayload) p).setFollowUpResponseExpirationDate(dueDate));

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD))
            .forEach(p -> ((PermitNotificationFollowUpRequestTaskPayload) p).setFollowUpResponseExpirationDate(dueDate));

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS_PAYLOAD))
            .forEach(p -> {
                PermitNotificationFollowUpReviewDecision reviewDecision = ((PermitNotificationFollowUpWaitForAmendsRequestTaskPayload) p).getReviewDecision();
                ((PermitNotificationFollowupRequiredChangesDecisionDetails) reviewDecision.getDetails()).setDueDate(dueDate);
            });

        requestTasks.stream().map(RequestTask::getPayload)
            .filter(p -> p.getPayloadType().equals(RequestTaskPayloadType.PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD))
            .forEach(p -> {
                PermitNotificationFollowUpReviewDecision reviewDecision = ((PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload) p).getReviewDecision();
                ((PermitNotificationFollowupRequiredChangesDecisionDetails) reviewDecision.getDetails()).setDueDate(dueDate);
            });
    }
}

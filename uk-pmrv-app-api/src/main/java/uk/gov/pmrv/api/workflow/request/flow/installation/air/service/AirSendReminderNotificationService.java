package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

@Service
@RequiredArgsConstructor
public class AirSendReminderNotificationService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER,
                NotificationTemplateWorkflowTaskType.AIR_SUBMIT);
    }

    public void sendSecondReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER,
                NotificationTemplateWorkflowTaskType.AIR_SUBMIT);
    }

    public void sendRespondFirstReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER,
                NotificationTemplateWorkflowTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS);
    }

    public void sendRespondSecondReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER,
                NotificationTemplateWorkflowTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS);
    }

    private void sendReminderNotification(final String requestId, 
                                          final Date deadline, 
                                          final ExpirationReminderType expirationType,
                                          final NotificationTemplateWorkflowTaskType workflowTaskType) {

        final Request request = requestService.findRequestById(requestId);
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
            .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        requestExpirationReminderService.sendExpirationReminderNotification(requestId,
                NotificationTemplateExpirationReminderParams.builder()
                        .workflowTask(workflowTaskType.getDescription())
                        .recipient(accountPrimaryContact)
                        .expirationTime(expirationType.getDescription())
                        .expirationTimeLong(expirationType.getDescriptionLong())
                        .deadline(deadline)
                        .build());
    }
}

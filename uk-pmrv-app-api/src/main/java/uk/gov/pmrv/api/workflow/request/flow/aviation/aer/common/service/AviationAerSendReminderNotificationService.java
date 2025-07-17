package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AviationAerSendReminderNotificationService {

    private final RequestService requestService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RequestExpirationReminderService requestExpirationReminderService;

    public void sendFirstReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.FIRST_REMINDER);
    }

    public void sendSecondReminderNotification(final String requestId, final Date deadline) {
        sendReminderNotification(requestId, deadline, ExpirationReminderType.SECOND_REMINDER);
    }

    private void sendReminderNotification(final String requestId, final Date deadline,
                                          final ExpirationReminderType expirationType) {
        final Request request = requestService.findRequestById(requestId);
        final UserInfoDTO accountPrimaryContact =
            requestAccountContactQueryService.getRequestAccountPrimaryContact(request)
                .orElseThrow(() -> new BusinessException(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND));

        requestExpirationReminderService.sendExpirationReminderNotification(requestId,
            NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.AVIATION_AER.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(expirationType.getDescription())
                .expirationTimeLong(expirationType.getDescriptionLong())
                .deadline(deadline)
                .build());
    }
}

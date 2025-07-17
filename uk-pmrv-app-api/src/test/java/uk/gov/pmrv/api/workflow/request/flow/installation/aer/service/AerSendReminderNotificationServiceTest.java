package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

import java.util.Date;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerSendReminderNotificationServiceTest {

    @InjectMocks
    private AerSendReminderNotificationService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {
        final String requestId = "AEM-001";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
            .workflowTask(NotificationTemplateWorkflowTaskType.AER.getDescription())
            .recipient(accountPrimaryContact)
            .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
            .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
            .deadline(deadline)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));

        service.sendFirstReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
            .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendSecondReminderNotification() {
        final String requestId = "AEM-001";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
            .workflowTask(NotificationTemplateWorkflowTaskType.AER.getDescription())
            .recipient(accountPrimaryContact)
            .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
            .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
            .deadline(deadline)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));

        service.sendSecondReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
            .sendExpirationReminderNotification(requestId, params);
    }
}
package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
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

@ExtendWith(MockitoExtension.class)
class AirSendReminderNotificationServiceTest {

    @InjectMocks
    private AirSendReminderNotificationService service;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {
        
        final String requestId = "AIR-2022-1";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.AIR_SUBMIT.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendFirstReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendSecondReminderNotification() {

        final String requestId = "AIR-2022-1";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.AIR_SUBMIT.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendSecondReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendRespondFirstReminderNotification() {

        final String requestId = "AIR-2022-1";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendRespondFirstReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendRespondSecondReminderNotification() {

        final String requestId = "AIR-2022-1";
        final Date deadline = new Date();
        final Request request = Request.builder().id(requestId).build();
        final UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        final NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
                .workflowTask(NotificationTemplateWorkflowTaskType.AIR_RESPOND_TO_REGULATOR_COMMENTS.getDescription())
                .recipient(accountPrimaryContact)
                .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
                .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
                .deadline(deadline)
                .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request)).thenReturn(Optional.of(accountPrimaryContact));

        // Invoke
        service.sendRespondSecondReminderNotification(requestId, deadline);

        // Verify
        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
                .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
                .sendExpirationReminderNotification(requestId, params);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.ExpirationReminderType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestExpirationReminderService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateExpirationReminderParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.NotificationTemplateWorkflowTaskType;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerSendReminderNotificationServiceTest {

    @InjectMocks
    private AviationAerSendReminderNotificationService sendReminderNotificationService;

    @Mock
    private RequestService requestService;

    @Mock
    private RequestAccountContactQueryService requestAccountContactQueryService;

    @Mock
    private RequestExpirationReminderService requestExpirationReminderService;

    @Test
    void sendFirstReminderNotification() {
        String requestId = "REQ-001";
        Date deadline = new Date();
        Request request = Request.builder().id(requestId).build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
            .workflowTask(NotificationTemplateWorkflowTaskType.AVIATION_AER.getDescription())
            .recipient(accountPrimaryContact)
            .expirationTime(ExpirationReminderType.FIRST_REMINDER.getDescription())
            .expirationTimeLong(ExpirationReminderType.FIRST_REMINDER.getDescriptionLong())
            .deadline(deadline)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));

        sendReminderNotificationService.sendFirstReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
            .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendFirstReminderNotification_no_primary_contact() {
        String requestId = "REQ-001";
        Date deadline = new Date();
        Request request = Request.builder().id(requestId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class,
            () -> sendReminderNotificationService.sendFirstReminderNotification(requestId, deadline));

        assertEquals(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND, be.getErrorCode());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verifyNoInteractions(requestExpirationReminderService);
    }

    @Test
    void sendSecondReminderNotification() {
        String requestId = "REQ-001";
        Date deadline = new Date();
        Request request = Request.builder().id(requestId).build();
        UserInfoDTO accountPrimaryContact = UserInfoDTO.builder().userId("userId").build();

        NotificationTemplateExpirationReminderParams params = NotificationTemplateExpirationReminderParams.builder()
            .workflowTask(NotificationTemplateWorkflowTaskType.AVIATION_AER.getDescription())
            .recipient(accountPrimaryContact)
            .expirationTime(ExpirationReminderType.SECOND_REMINDER.getDescription())
            .expirationTimeLong(ExpirationReminderType.SECOND_REMINDER.getDescriptionLong())
            .deadline(deadline)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.of(accountPrimaryContact));

        sendReminderNotificationService.sendSecondReminderNotification(requestId, deadline);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verify(requestExpirationReminderService, times(1))
            .sendExpirationReminderNotification(requestId, params);
    }

    @Test
    void sendSecondReminderNotification_no_primary_contact() {
        String requestId = "REQ-001";
        Date deadline = new Date();
        Request request = Request.builder().id(requestId).build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestAccountContactQueryService.getRequestAccountPrimaryContact(request))
            .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class,
            () -> sendReminderNotificationService.sendFirstReminderNotification(requestId, deadline));

        assertEquals(ErrorCode.ACCOUNT_CONTACT_TYPE_PRIMARY_CONTACT_NOT_FOUND, be.getErrorCode());

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestAccountContactQueryService, times(1))
            .getRequestAccountPrimaryContact(request);
        verifyNoInteractions(requestExpirationReminderService);
    }
}
package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.competentauthority.CompetentAuthorityEnum.ENGLAND;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus.IN_PROGRESS;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType.SYSTEM_MESSAGE_NOTIFICATION;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.notificationapi.domain.NotificationContent;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestPayload;

@ExtendWith(MockitoExtension.class)
class SendSystemMessageNotificationRequestServiceTest {

    @InjectMocks
    private SendSystemMessageNotificationRequestService cut;

    @Mock
    private RequestTaskRepository requestTaskRepository;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private StartProcessRequestService startProcessRequestService;

    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Test
    void send() {
        final RequestTaskType requestTaskType = ACCOUNT_USERS_SETUP;
        final Long accountId = 1L;
        final CompetentAuthorityEnum ca = ENGLAND;
        final Long verificationBodyId = 1L;
        final String notificationMessageRecipient = "operId";
        final String notificationSubject = "subject";
        final String notificationText = "subject";

        final SystemNotificationInfo systemMessageNotificationInfo = SystemNotificationInfo.builder()
                .template(PmrvNotificationTemplateName.ACCOUNT_USERS_SETUP.getName())
                .accountId(accountId)
                .receiver(notificationMessageRecipient)
                .build();
        final NotificationContent notificationContent = NotificationContent.builder()
                        .subject(notificationSubject)
                        .text(notificationText)
                        .build();
        final UserRoleTypeDTO recipientUserRoleType = UserRoleTypeDTO.builder()
            .userId(notificationMessageRecipient)
            .roleType(RoleTypeConstants.OPERATOR)
            .build();

        final SystemMessageNotificationRequestPayload requestPayload = SystemMessageNotificationRequestPayload.builder()
            .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
            .messagePayload(SystemMessageNotificationPayload.builder()
                .subject(notificationSubject)
                .text(notificationText)
                .build())
            .operatorAssignee(notificationMessageRecipient)
            .build();

        final RequestParams requestParams = RequestParams.builder()
            .type(SYSTEM_MESSAGE_NOTIFICATION)
            .accountId(accountId)
            .requestPayload(requestPayload)
            .build();

        final Request request =
            createRequest(accountId, SYSTEM_MESSAGE_NOTIFICATION, IN_PROGRESS, ca, verificationBodyId);

        //mock
        when(userRoleTypeService.getUserRoleTypeByUserId(notificationMessageRecipient)).thenReturn(recipientUserRoleType);
        when(startProcessRequestService.startSystemMessageNotificationProcess(requestParams, requestTaskType)).thenReturn(request);

        //invoke
        cut.send(systemMessageNotificationInfo, notificationContent);

        verify(startProcessRequestService, times(1)).startSystemMessageNotificationProcess(requestParams, requestTaskType);
    }

    private Request createRequest(Long accountId, RequestType type, RequestStatus status,
            CompetentAuthorityEnum ca, Long verificationBodyId) {
        return Request.builder()
            .id("1")
            .competentAuthority(ca)
            .verificationBodyId(verificationBodyId)
            .type(type)
            .status(status)
            .accountId(accountId)
            .creationDate(LocalDateTime.now())
            .build();

    }

}

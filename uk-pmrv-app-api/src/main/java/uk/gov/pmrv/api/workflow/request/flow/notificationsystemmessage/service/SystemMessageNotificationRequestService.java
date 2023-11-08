package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.notification.message.domain.SystemMessageNotificationInfo;
import uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType;
import uk.gov.pmrv.api.notification.message.service.SystemMessageNotificationSendService;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestTaskRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.mapper.SystemMessageNotificationParamsMapper;

import java.util.Map;

import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.ACCOUNT_USERS_SETUP;
import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.NEW_VERIFICATION_BODY_EMITTER;
import static uk.gov.pmrv.api.notification.message.domain.enumeration.SystemMessageNotificationType.VERIFIER_NO_LONGER_AVAILABLE;


@RequiredArgsConstructor
@Service
public class SystemMessageNotificationRequestService implements SystemMessageNotificationSendService {

    private final RequestTaskRepository requestTaskRepository;
    private final StartProcessRequestService startProcessRequestService;
    private final WorkflowService workflowService;
    private final UserRoleTypeService userRoleTypeService;
    private static final SystemMessageNotificationParamsMapper notificationSystemMessageParamsMapper =
        Mappers.getMapper(SystemMessageNotificationParamsMapper.class);
    private static final Map<SystemMessageNotificationType, RequestTaskType> systemMessageNotificationTypeRequestTaskTypeMap;

    static {
        systemMessageNotificationTypeRequestTaskTypeMap = Map.of(ACCOUNT_USERS_SETUP, RequestTaskType.ACCOUNT_USERS_SETUP,
                NEW_VERIFICATION_BODY_EMITTER, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER,
                VERIFIER_NO_LONGER_AVAILABLE, RequestTaskType.VERIFIER_NO_LONGER_AVAILABLE);
    }

    @Override
    @Transactional
    public void sendNotificationSystemMessage(SystemMessageNotificationInfo msgInfo, NotificationContent notificationContent) {
        SystemMessageNotificationRequestParams params = SystemMessageNotificationRequestParams.builder()
                .requestTaskType(systemMessageNotificationTypeRequestTaskTypeMap.get(msgInfo.getMessageType()))
                .accountId(msgInfo.getAccountId())
                .notificationMessageRecipient(msgInfo.getReceiver())
                .notificationContent(notificationContent)
                .build();
        
        UserRoleTypeDTO recipientRoleType = userRoleTypeService.getUserRoleTypeByUserId(params.getNotificationMessageRecipient());
        RequestParams requestParams = notificationSystemMessageParamsMapper.toRequestParams(params, recipientRoleType.getRoleType());
        startProcessRequestService.startSystemMessageNotificationProcess(requestParams, params.getRequestTaskType());
    }

    public void completeOpenSystemMessageNotificationRequests(String assignee) {
        requestTaskRepository
            .findByRequestTypeAndAssignee(RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee)
            .forEach(rt -> workflowService.completeTask(rt.getProcessTaskId()));
    }

    public void completeOpenSystemMessageNotificationRequests(String assignee, Long accountId) {
        requestTaskRepository.findByRequestTypeAndAssigneeAndRequestAccountId(
            RequestType.SYSTEM_MESSAGE_NOTIFICATION, assignee, accountId)
            .forEach(rt -> workflowService.completeTask(rt.getProcessTaskId()));
    }


}

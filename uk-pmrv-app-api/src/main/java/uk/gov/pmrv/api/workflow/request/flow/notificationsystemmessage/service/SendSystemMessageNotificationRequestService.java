package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.authorization.core.domain.dto.UserRoleTypeDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.notificationapi.domain.NotificationContent;
import uk.gov.netz.api.notificationapi.system.SendSystemNotificationService;
import uk.gov.netz.api.notificationapi.system.SystemNotificationInfo;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestParams;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.mapper.SystemMessageNotificationParamsMapper;


@RequiredArgsConstructor
@Service
public class SendSystemMessageNotificationRequestService implements SendSystemNotificationService {

    private final StartProcessRequestService startProcessRequestService;
    private final UserRoleTypeService userRoleTypeService;
    private static final SystemMessageNotificationParamsMapper notificationSystemMessageParamsMapper =
        Mappers.getMapper(SystemMessageNotificationParamsMapper.class);

    @Override
    @Transactional
    public void send(SystemNotificationInfo msgInfo, NotificationContent notificationContent) {
        SystemMessageNotificationRequestParams params = SystemMessageNotificationRequestParams.builder()
				.requestTaskType(SystemMessageNotificationTemplateRequestTaskTypeMappings
						.getRequestTaskTypePrefix(PmrvNotificationTemplateName.getEnumValueFromName(msgInfo.getTemplate())))
                .accountId(msgInfo.getAccountId())
                .notificationMessageRecipient(msgInfo.getReceiver())
                .notificationContent(notificationContent)
                .build();
        
        UserRoleTypeDTO recipientRoleType = userRoleTypeService.getUserRoleTypeByUserId(params.getNotificationMessageRecipient());
        RequestParams requestParams = notificationSystemMessageParamsMapper.toRequestParams(params, recipientRoleType.getRoleType());
        startProcessRequestService.startSystemMessageNotificationProcess(requestParams, params.getRequestTaskType());
    }

}

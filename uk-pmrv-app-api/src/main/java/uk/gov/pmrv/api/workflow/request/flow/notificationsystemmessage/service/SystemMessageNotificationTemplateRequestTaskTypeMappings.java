package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.service;

import java.util.Map;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

final class SystemMessageNotificationTemplateRequestTaskTypeMappings {

	private static final Map<PmrvNotificationTemplateName, RequestTaskType> SYSTEM_MESSAGE_NOTIFICATION_TEMPLATE_REQUEST_TASK_TYPE_MAP = Map
			.of(
				PmrvNotificationTemplateName.ACCOUNT_USERS_SETUP, RequestTaskType.ACCOUNT_USERS_SETUP,
				PmrvNotificationTemplateName.NEW_VERIFICATION_BODY_EMITTER, RequestTaskType.NEW_VERIFICATION_BODY_EMITTER,
				PmrvNotificationTemplateName.VERIFIER_NO_LONGER_AVAILABLE, RequestTaskType.VERIFIER_NO_LONGER_AVAILABLE
			);

	private SystemMessageNotificationTemplateRequestTaskTypeMappings() {
	}

	public static RequestTaskType getRequestTaskTypePrefix(PmrvNotificationTemplateName notificationTemplateName) {
		return SYSTEM_MESSAGE_NOTIFICATION_TEMPLATE_REQUEST_TASK_TYPE_MAP.get(notificationTemplateName);
	}

	public static Map<PmrvNotificationTemplateName, RequestTaskType> getAllMappings() {
		return SYSTEM_MESSAGE_NOTIFICATION_TEMPLATE_REQUEST_TASK_TYPE_MAP;
	}

}

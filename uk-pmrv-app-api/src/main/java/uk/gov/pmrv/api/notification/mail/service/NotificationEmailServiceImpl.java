package uk.gov.pmrv.api.notification.mail.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;
import uk.gov.netz.api.notificationapi.mail.service.SendEmailService;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

@Log4j2
@Service
@ConditionalOnProperty(name = "env.isProd", havingValue = "true")
public class NotificationEmailServiceImpl extends NotificationEmailBaseServiceImpl<EmailNotificationTemplateData> {

	public NotificationEmailServiceImpl(SendEmailService sendEmailService,
			NotificationTemplateProcessService notificationTemplateProcessService,
			NotificationProperties notificationProperties) {
		super(sendEmailService, notificationTemplateProcessService, notificationProperties);
	}

	@Override
	protected NotificationContent buildNotificationContent(EmailNotificationTemplateData notificationTemplateData) {
		return notificationTemplateProcessService.processEmailNotificationTemplate(
				notificationTemplateData.getTemplateName(), 
				notificationTemplateData.getCompetentAuthority(), 
				null,
				notificationTemplateData.getTemplateParams());
	}

}

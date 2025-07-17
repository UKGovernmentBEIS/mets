package uk.gov.pmrv.api.notification.mail.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import uk.gov.netz.api.notificationapi.mail.config.property.NotificationProperties;
import uk.gov.netz.api.notificationapi.mail.service.SendEmailService;
import uk.gov.pmrv.api.notification.mail.domain.PmrvEmailNotificationTemplateData;
import uk.gov.pmrv.api.notification.template.domain.NotificationContent;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateProcessService;

@Service
@ConditionalOnProperty(name = "env.isProd", havingValue = "true")
public class PmrvNotificationEmailServiceImpl extends NotificationEmailBaseServiceImpl<PmrvEmailNotificationTemplateData> {

	public PmrvNotificationEmailServiceImpl(SendEmailService sendEmailService,
			NotificationTemplateProcessService notificationTemplateProcessService,
			NotificationProperties notificationProperties) {
		super(sendEmailService, notificationTemplateProcessService, notificationProperties);
	}

	@Override
	protected NotificationContent buildNotificationContent(PmrvEmailNotificationTemplateData notificationTemplateData) {
		return notificationTemplateProcessService.processEmailNotificationTemplate(
            	notificationTemplateData.getTemplateName(),
                notificationTemplateData.getCompetentAuthority(),
                notificationTemplateData.getAccountType(),
                notificationTemplateData.getTemplateParams());
	}
    
}

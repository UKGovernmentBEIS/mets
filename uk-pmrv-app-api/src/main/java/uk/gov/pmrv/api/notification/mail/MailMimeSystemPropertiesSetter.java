package uk.gov.pmrv.api.notification.mail;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class MailMimeSystemPropertiesSetter {

	@PostConstruct
	public void setProperties() {
		System.setProperty("mail.mime.splitlongparameters", "false");
	}
}

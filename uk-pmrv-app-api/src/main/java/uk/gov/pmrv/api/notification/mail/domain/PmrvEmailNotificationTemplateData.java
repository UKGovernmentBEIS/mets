package uk.gov.pmrv.api.notification.mail.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.notificationapi.mail.domain.EmailNotificationTemplateData;

@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PmrvEmailNotificationTemplateData extends EmailNotificationTemplateData {

	private AccountType accountType;

}

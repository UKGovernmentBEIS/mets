package uk.gov.pmrv.api.notification.mail.domain;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.notification.template.domain.enumeration.NotificationTemplateName;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class EmailNotificationTemplateData {

    private CompetentAuthorityEnum competentAuthority;
    
    private NotificationTemplateName templateName;

    private AccountType accountType;
    
    @Builder.Default
    private Map<String, Object> templateParams = new HashMap<>();
    
}

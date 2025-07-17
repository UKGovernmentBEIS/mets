package uk.gov.pmrv.api.notification.template.domain.converter;

import jakarta.persistence.AttributeConverter;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

/**
 * Converts PmrvNotificationTemplateName value into string and back again.
 */
public class NotificationTemplateNameConverter implements AttributeConverter<PmrvNotificationTemplateName, String> {

    @Override
    public String convertToDatabaseColumn(PmrvNotificationTemplateName notificationTemplateName) {
        return notificationTemplateName.getName();
    }

    @Override
    public PmrvNotificationTemplateName convertToEntityAttribute(String dbData) {
        return PmrvNotificationTemplateName.getEnumValueFromName(dbData);
    }
}

package uk.gov.pmrv.api.notification.message.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

@Getter
@AllArgsConstructor
public enum SystemMessageNotificationType {
    ACCOUNT_USERS_SETUP(PmrvNotificationTemplateName.ACCOUNT_USERS_SETUP),
    NEW_VERIFICATION_BODY_EMITTER(PmrvNotificationTemplateName.NEW_VERIFICATION_BODY_EMITTER),
    VERIFIER_NO_LONGER_AVAILABLE(PmrvNotificationTemplateName.VERIFIER_NO_LONGER_AVAILABLE);

    private final PmrvNotificationTemplateName notificationTemplateName;
}

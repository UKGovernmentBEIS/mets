package uk.gov.pmrv.api.user.core.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.netz.api.token.JwtTokenAction;
import uk.gov.pmrv.api.notification.template.domain.enumeration.PmrvNotificationTemplateName;

import java.util.Map;

/**
 * Object containing information to send a notification with a redirection link to user.
 */
@Data
@Builder
public class UserNotificationWithRedirectionLinkInfo {

    private PmrvNotificationTemplateName templateName;
    private String userEmail;
    private Map<String, Object> notificationParams;
    private String linkParamName;
    private String linkPath;
    private TokenParams tokenParams;

    @Getter
    @EqualsAndHashCode
    @Builder
    public static class TokenParams {
        private final JwtTokenAction jwtTokenAction;
        private final String claimValue;
        private final long expirationInterval;
    }
}

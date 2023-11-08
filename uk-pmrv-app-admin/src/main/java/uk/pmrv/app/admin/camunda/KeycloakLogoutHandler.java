package uk.pmrv.app.admin.camunda;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class KeycloakLogoutHandler implements LogoutSuccessHandler {
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private String oauth2UserLogoutUri;

    public KeycloakLogoutHandler(@Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri:}") String oauth2UserAuthorizationUri) {
        if (!StringUtils.isEmpty(oauth2UserAuthorizationUri)) {
            // in order to get the valid logout uri: simply replace "/auth" at the end of the user authorization uri with "/logout"
            this.oauth2UserLogoutUri = oauth2UserAuthorizationUri.replace("openid-connect/auth", "openid-connect/logout");
        }
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        if (!StringUtils.isEmpty(oauth2UserLogoutUri)) {
            // Calculate redirect URI for Keycloak, something like http://<host:port>/camunda
            String requestUrl = request.getRequestURL().toString();
            String redirectUri = requestUrl.substring(0, requestUrl.indexOf("/app"));
            // Complete logout URL
            String logoutUrl = oauth2UserLogoutUri + "?post_logout_redirect_uri=" + redirectUri + "&id_token_hint=" + ((OidcUser)authentication.getPrincipal()).getIdToken().getTokenValue();

            // Do logout by redirecting to Keycloak logout
            redirectStrategy.sendRedirect(request, response, logoutUrl);
        }
    }
}

package uk.gov.pmrv.api.user.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.netz.api.restclient.RestClientEndPoint;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakSignature;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserDetails;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakUserInfo;

import java.util.List;

/**
 * The Keycloak client rest points enum.
 */
@Getter
@AllArgsConstructor
public enum KeycloakRestEndPointEnum implements RestClientEndPoint {

    /** Return users registered in Keycloak. */
    KEYCLOAK_GET_USERS("/users", HttpMethod.POST, new ParameterizedTypeReference<List<KeycloakUserInfo>>() {}),
    KEYCLOAK_GET_USER_DETAILS("/users/user/details", HttpMethod.GET, new ParameterizedTypeReference<KeycloakUserDetails>() {}),
    KEYCLOAK_POST_USER_DETAILS("/users/user/details", HttpMethod.POST, new ParameterizedTypeReference<Void>() {}),
    KEYCLOAK_GET_USER_SIGNATURE("/users/user/signature", HttpMethod.GET, new ParameterizedTypeReference<KeycloakSignature>() {}),
    KEYCLOAK_VALIDATE_OTP("/users/otp/validation", HttpMethod.POST, new ParameterizedTypeReference<Void>() {}),
    ;

    private final String path;
    private final HttpMethod method;
    private final ParameterizedTypeReference<?> parameterizedTypeReference;
}

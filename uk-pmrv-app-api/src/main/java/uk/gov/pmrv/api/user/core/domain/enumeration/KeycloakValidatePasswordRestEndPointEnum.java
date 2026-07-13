package uk.gov.pmrv.api.user.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.netz.api.restclient.RestClientEndPoint;
import uk.gov.pmrv.api.user.core.domain.model.keycloak.KeycloakPasswordValidationResponse;

/**
 * The Keycloak client rest points enum.
 */
@Getter
@AllArgsConstructor
public enum KeycloakValidatePasswordRestEndPointEnum implements RestClientEndPoint<KeycloakPasswordValidationResponse> {

    /** Return users registered in Keycloak. */
    KEYCLOAK_VALIDATE_PASSWORD("/validate-password", HttpMethod.POST, new ParameterizedTypeReference<KeycloakPasswordValidationResponse>() {})
    ;

    /** The url. */
    private final String path;

    /** The {@link HttpMethod}. */
    private final HttpMethod method;

    /** The {@link ParameterizedTypeReference}. */
    private final ParameterizedTypeReference<?> parameterizedTypeReference;
}

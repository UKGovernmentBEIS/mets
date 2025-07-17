package uk.gov.pmrv.api.user.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import uk.gov.netz.api.restclient.RestClientEndPoint;

/**
 * The Pwned passwords rest points enum.
 */
@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestClientEndPoint {

    /** Protect the value of the source password being searched for. */
    PWNED_PASSWORDS("/range/{passwordHash}", HttpMethod.GET, new ParameterizedTypeReference<String>() {});

    private final String path;
    private final HttpMethod method;
    private final ParameterizedTypeReference<?> parameterizedTypeReference;

}

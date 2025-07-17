package uk.gov.pmrv.api.notification.template.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.netz.api.restclient.RestClientEndPoint;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public enum RestEndPointEnum implements RestClientEndPoint {

    GENERATE("/generate", HttpMethod.POST, new ParameterizedTypeReference<byte[]>() {});

    private final String path;
    private final HttpMethod method;
    private final ParameterizedTypeReference<?> parameterizedTypeReference;
}

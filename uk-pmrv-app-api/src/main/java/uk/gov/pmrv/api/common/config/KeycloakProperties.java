package uk.gov.pmrv.api.common.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {
    @JsonProperty("auth-server-url")
    protected String authServerUrl;

    @JsonProperty("realm")
    protected String realm;

    @JsonProperty("client-id")
    protected String clientId;

    @JsonProperty("client-secret")
    protected String clientSecret;
}

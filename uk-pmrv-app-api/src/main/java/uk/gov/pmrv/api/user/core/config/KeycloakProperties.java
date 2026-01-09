package uk.gov.pmrv.api.user.core.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "keycloak")
@Getter
@Setter
public class KeycloakProperties {
    @Valid
    @NotEmpty
    @JsonProperty("auth-server-url")
    protected String authServerUrl;

    @Valid
    @NotEmpty
    @JsonProperty("realm")
    protected String realm;

    @Valid
    @NotEmpty
    @JsonProperty("client-id")
    protected String clientId;

    @Valid
    @NotEmpty
    @JsonProperty("client-secret")
    protected String clientSecret;
}

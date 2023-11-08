package uk.gov.pmrv.api.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "registry-administrator")
@Data
public class RegistryConfig {

    @NotBlank
    private String email;
}

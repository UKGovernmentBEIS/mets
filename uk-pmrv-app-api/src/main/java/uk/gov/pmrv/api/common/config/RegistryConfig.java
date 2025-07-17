package uk.gov.pmrv.api.common.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "registry-administrator")
@Data
public class RegistryConfig {

    @Valid
    @NotBlank
    private String email;
}

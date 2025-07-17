package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "installation.registry.integration.error.handle")
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationRegistryIntegrationEmailProperties {

    @NotNull
    private Map<String, String> email = new HashMap<>();
}

package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "installation.registry.integration.error.handle")
@ConditionalOnExpression("'${registry.integration.emissions.updated.enabled:false}' == 'true' or " +
        "'${registry.integration.set.operator.id.enabled:false}' == 'true' or " +
        "'${registry.integration.account.creation.enabled:false}' == 'true' or " +
        "'${registry.integration.account.update.enabled:false}' == 'true'")
public class InstallationRegistryIntegrationEmailProperties {

    @NotNull
    private Map<String, String> email = new HashMap<>();
}
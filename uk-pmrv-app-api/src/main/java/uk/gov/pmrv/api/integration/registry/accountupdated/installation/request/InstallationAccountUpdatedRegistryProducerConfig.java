package uk.gov.pmrv.api.integration.registry.accountupdated.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountUpdatedRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, AccountUpdatingEvent> netzKafkaProducerFactory;
    private final InstallationProducerConfigProperties installationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, AccountUpdatingEvent> installationAccountUpdatedKafkaTemplate() {
        return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
    }

}

package uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountContactRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, MetsContactsEvent> netzKafkaProducerFactory;
    private final InstallationProducerConfigProperties installationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, MetsContactsEvent> installationAccountContactKafkaTemplate() {
        return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
    }


}

package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;


@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountCreatedSendToRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, AccountOpeningEvent> netzKafkaProducerFactory;
    private final InstallationProducerConfigProperties installationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, AccountOpeningEvent> installationAccountCreatedKafkaTemplate(
            KafkaCorrelationHeaderProducerInterceptor<String, AccountOpeningEvent> correlationHeaderInterceptor,
            KafkaCorrelationParentHeaderProducerInterceptor<String, AccountOpeningEvent> correlationParentHeaderInterceptor) {
        return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
    }

}

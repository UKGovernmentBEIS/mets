package uk.gov.pmrv.api.integration.registry.accountcontacts.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.integration.registry.common.AviationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountContactRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, MetsContactsEvent> netzKafkaProducerFactory;
    private final AviationProducerConfigProperties aviationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, MetsContactsEvent> aviationAccountContactKafkaTemplate(
            KafkaCorrelationHeaderProducerInterceptor<String, MetsContactsEvent> correlationHeaderInterceptor,
            KafkaCorrelationParentHeaderProducerInterceptor<String, MetsContactsEvent> correlationParentHeaderInterceptor) {
        return netzKafkaProducerFactory.createKafkaTemplate(aviationProducerConfigProperties);
    }


}

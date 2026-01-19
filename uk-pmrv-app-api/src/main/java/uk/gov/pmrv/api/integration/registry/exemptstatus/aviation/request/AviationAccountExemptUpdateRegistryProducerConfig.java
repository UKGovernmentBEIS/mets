package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.pmrv.api.integration.registry.common.AviationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptUpdateRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, AccountExemptionUpdateEvent> netzKafkaProducerFactory;
    private final AviationProducerConfigProperties aviationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, AccountExemptionUpdateEvent> aviationAccountExemptUpdateKafkaTemplate(
            KafkaCorrelationHeaderProducerInterceptor<String, AccountExemptionUpdateEvent> correlationHeaderInterceptor,
            KafkaCorrelationParentHeaderProducerInterceptor<String, AccountExemptionUpdateEvent> correlationParentHeaderInterceptor) {
        return netzKafkaProducerFactory.createKafkaTemplate(aviationProducerConfigProperties);
    }

}

package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class AviationSetOperatorIdSendToRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, OperatorUpdateEventOutcome> netzKafkaProducerFactory;
    private final AviationProducerConfigProperties aviationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, OperatorUpdateEventOutcome> aviationSetOperatorIdKafkaTemplate() {
        return netzKafkaProducerFactory.createKafkaTemplate(aviationProducerConfigProperties);
    }

}

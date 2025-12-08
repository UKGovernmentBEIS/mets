package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class AviationSetOperatorIdKafkaConsumerConfig {

    private final AviationConsumerConfigProperties aviationConsumerConfigProperties;
    private final NetzKafkaConsumerFactory<String, OperatorUpdateEvent> netzKafkaConsumerFactory;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, OperatorUpdateEvent> aviationSetOperatorIdKafkaListenerContainerFactory(
            @Value("${kafka.aviation.operator-identifier-response.group}") String groupId) {
        return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId, aviationConsumerConfigProperties,
                OperatorUpdateEvent.class);
    }


}

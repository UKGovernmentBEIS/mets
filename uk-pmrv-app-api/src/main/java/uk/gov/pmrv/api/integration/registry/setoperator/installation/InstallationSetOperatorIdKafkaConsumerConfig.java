package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.pmrv.api.integration.registry.common.InstallationConsumerConfigProperties;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdResponseEvent;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationSetOperatorIdKafkaConsumerConfig {

    private final InstallationConsumerConfigProperties installationConsumerConfigProperties;
    private final NetzKafkaConsumerFactory<String, SetOperatorIdResponseEvent> netzKafkaConsumerFactory;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, SetOperatorIdResponseEvent> installationSetOperatorIdKafkaListenerContainerFactory(
            @Value("${kafka.installation.operator-identifier-response.group}") String groupId) {
        return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId, installationConsumerConfigProperties,
                SetOperatorIdResponseEvent.class);
    }


}

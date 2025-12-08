package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationCreatedKafkaConsumerConfig {

    private final AviationConsumerConfigProperties aviationConsumerConfigProperties;
    private final NetzKafkaConsumerFactory<String, AccountOpeningEventOutcome> netzKafkaConsumerFactory;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountOpeningEventOutcome> aviationAccountCreatedKafkaListenerContainerFactory(
            @Value("${kafka.aviation.account-created-response.group}") String groupId) {
        return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId, aviationConsumerConfigProperties,
                AccountOpeningEventOutcome.class);
    }


}

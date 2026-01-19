package uk.gov.pmrv.api.integration.registry.accountcontacts.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountContactKafkaConsumerConfig {

    private final NetzKafkaConsumerFactory<String, MetsContactsEventOutcome> netzKafkaConsumerFactory;
    private final AviationConsumerConfigProperties aviationConsumerConfigProperties;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, MetsContactsEventOutcome> aviationAccountContactKafkaListenerContainerFactory(
            @Value("${kafka.aviation.account-contact-response.group}") String groupId) {
        return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
                aviationConsumerConfigProperties, MetsContactsEventOutcome.class);
    }

}

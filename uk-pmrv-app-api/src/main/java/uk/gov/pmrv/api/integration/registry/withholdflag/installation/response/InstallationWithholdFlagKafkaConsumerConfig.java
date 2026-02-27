package uk.gov.pmrv.api.integration.registry.withholdflag.installation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.InstallationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagKafkaConsumerConfig {

    private final NetzKafkaConsumerFactory<String, AccountWithholdUpdateEventOutcome> netzKafkaConsumerFactory;
    private final InstallationConsumerConfigProperties installationConsumerConfigProperties;

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, AccountWithholdUpdateEventOutcome> installationWithholdFlagKafkaListenerContainerFactory(
            @Value("${kafka.installation.withhold-flag-response.group}") String groupId) {
        return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
                installationConsumerConfigProperties, AccountWithholdUpdateEventOutcome.class);
    }


}

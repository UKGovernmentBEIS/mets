package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, AccountWithholdUpdateEvent> netzKafkaProducerFactory;
    private final InstallationProducerConfigProperties installationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, AccountWithholdUpdateEvent> withholdFlagKafkaTemplate(
            KafkaCorrelationHeaderProducerInterceptor<String, AccountWithholdUpdateEvent> correlationHeaderInterceptor,
            KafkaCorrelationParentHeaderProducerInterceptor<String, AccountWithholdUpdateEvent> correlationParentHeaderInterceptor) {
        return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
    }

}

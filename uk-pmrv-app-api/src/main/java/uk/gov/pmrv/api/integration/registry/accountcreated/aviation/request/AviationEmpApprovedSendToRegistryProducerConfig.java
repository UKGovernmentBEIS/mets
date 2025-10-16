package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.pmrv.api.integration.registry.common.AviationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationEmpApprovedSendToRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, AviationAccountCreatedRegistryDTO> netzKafkaProducerFactory;
    private final AviationProducerConfigProperties aviationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, AviationAccountCreatedRegistryDTO> aviationAccountCreatedKafkaTemplate(
            KafkaCorrelationHeaderProducerInterceptor<String, AviationAccountCreatedRegistryDTO> correlationHeaderInterceptor,
            KafkaCorrelationParentHeaderProducerInterceptor<String, AviationAccountCreatedRegistryDTO> correlationParentHeaderInterceptor) {
        return netzKafkaProducerFactory.createKafkaTemplate(aviationProducerConfigProperties);
    }

}
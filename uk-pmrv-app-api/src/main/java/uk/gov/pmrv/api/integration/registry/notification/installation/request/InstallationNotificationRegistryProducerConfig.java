package uk.gov.pmrv.api.integration.registry.notification.installation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationNotificationRegistryProducerConfig {

    private final NetzKafkaProducerFactory<String, RegulatorNoticeEvent> netzKafkaProducerFactory;
    private final InstallationProducerConfigProperties installationProducerConfigProperties;

    @Bean
    KafkaTemplate<String, RegulatorNoticeEvent> noticeKafkaTemplate() {
        return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
    }

}

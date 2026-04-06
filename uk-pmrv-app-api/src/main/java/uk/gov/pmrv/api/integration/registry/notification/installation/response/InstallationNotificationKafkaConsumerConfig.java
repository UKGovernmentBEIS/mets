package uk.gov.pmrv.api.integration.registry.notification.installation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.InstallationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationNotificationKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, RegulatorNoticeEventOutcome> netzKafkaConsumerFactory;
	private final InstallationConsumerConfigProperties installationConsumerConfigProperties;

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEventOutcome> installationNotificationKafkaListenerContainerFactory(
			@Value("${kafka.installation.notification-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
				installationConsumerConfigProperties, RegulatorNoticeEventOutcome.class);
	}
}
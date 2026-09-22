package uk.gov.pmrv.api.integration.registry.notification.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class AviationNotificationKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, RegulatorNoticeEventOutcome> netzKafkaConsumerFactory;
	private final AviationConsumerConfigProperties aviationConsumerConfigProperties;

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, RegulatorNoticeEventOutcome> aviationNotificationKafkaListenerContainerFactory(
			@Value("${kafka.aviation.notification-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
				aviationConsumerConfigProperties, RegulatorNoticeEventOutcome.class);
	}
}
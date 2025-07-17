package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class AviationKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, AccountEmissionsUpdatedResponseEvent> netzKafkaConsumerFactory;
	private final AviationConsumerConfigProperties aviationConsumerConfigProperties;

	@Bean
	ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdatedResponseEvent> emissionsAccountResponseAviationKafkaListenerContainerFactory(
			@Value("${kafka.aviation.account-emissions-updated-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId, aviationConsumerConfigProperties,
				AccountEmissionsUpdatedResponseEvent.class);
	}
}
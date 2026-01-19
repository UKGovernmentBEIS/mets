package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountUpdatedKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, AccountUpdatingEventOutcome> netzKafkaConsumerFactory;
	private final AviationConsumerConfigProperties aviationConsumerConfigProperties;
	
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, AccountUpdatingEventOutcome> aviationAccountUpdatedKafkaListenerContainerFactory(
			@Value("${kafka.aviation.account-updated-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
				aviationConsumerConfigProperties, AccountUpdatingEventOutcome.class);
	}
}
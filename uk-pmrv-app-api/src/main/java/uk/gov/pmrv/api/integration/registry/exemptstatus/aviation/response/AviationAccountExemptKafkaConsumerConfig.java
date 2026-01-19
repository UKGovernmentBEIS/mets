package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import uk.gov.pmrv.api.integration.registry.common.AviationConsumerConfigProperties;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, AccountExemptionUpdateEventOutcome> netzKafkaConsumerFactory;
	private final AviationConsumerConfigProperties aviationConsumerConfigProperties;
	
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, AccountExemptionUpdateEventOutcome> aviationAccountExemptKafkaListenerContainerFactory(
			@Value("${kafka.aviation.account-exempt-update-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
				aviationConsumerConfigProperties, AccountExemptionUpdateEventOutcome.class);
	}
}
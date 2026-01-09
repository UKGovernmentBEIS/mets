package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.kafka.consumer.NetzKafkaConsumerFactory;
import uk.gov.pmrv.api.integration.registry.common.InstallationConsumerConfigProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationKafkaConsumerConfig {
	
	private final NetzKafkaConsumerFactory<String, AccountEmissionsUpdatedResponseEvent> netzKafkaConsumerFactory;
	private final InstallationConsumerConfigProperties installationConsumerConfigProperties;
	
	@Bean
	ConcurrentKafkaListenerContainerFactory<String, AccountEmissionsUpdatedResponseEvent> emissionsAccountResponseInstallationKafkaListenerContainerFactory(
			@Value("${kafka.installation.account-emissions-updated-response.group}") String groupId) {
		return netzKafkaConsumerFactory.createKafkaListenerContainerFactory(groupId,
				installationConsumerConfigProperties, AccountEmissionsUpdatedResponseEvent.class);
	}

}
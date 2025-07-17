package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.KafkaCorrelationParentHeaderProducerInterceptor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.pmrv.api.integration.registry.common.InstallationProducerConfigProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationReportableEmissionsUpdatedKafkaProducerConfig {

	private final NetzKafkaProducerFactory<String, AccountEmissionsUpdatedRequestEvent> netzKafkaProducerFactory;
	private final InstallationProducerConfigProperties installationProducerConfigProperties;

	@Bean
	KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> installationAccountEmissionsUpdatedKafkaTemplate(
			KafkaCorrelationHeaderProducerInterceptor<String, AccountEmissionsUpdatedRequestEvent> correlationHeaderInterceptor,
			KafkaCorrelationParentHeaderProducerInterceptor<String, AccountEmissionsUpdatedRequestEvent> correlationParentHeaderInterceptor) {
		return netzKafkaProducerFactory.createKafkaTemplate(installationProducerConfigProperties);
	}
	
}
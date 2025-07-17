package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import lombok.RequiredArgsConstructor;
import uk.gov.netz.api.kafka.producer.NetzKafkaProducerFactory;
import uk.gov.pmrv.api.integration.registry.common.AviationProducerConfigProperties;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class AviationReportableEmissionsUpdatedKafkaProducerConfig {

	private final NetzKafkaProducerFactory<String, AccountEmissionsUpdatedRequestEvent> netzKafkaProducerFactory;
	private final AviationProducerConfigProperties aviationProducerConfigProperties;

	@Bean
	KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> aviationAccountEmissionsUpdatedKafkaTemplate() {
		return netzKafkaProducerFactory.createKafkaTemplate(aviationProducerConfigProperties);
	}

}
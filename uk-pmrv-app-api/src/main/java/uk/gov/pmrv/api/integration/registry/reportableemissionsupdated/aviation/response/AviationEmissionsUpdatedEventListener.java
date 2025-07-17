package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.response;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.log4j.Log4j2;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedResponseEvent;

@Log4j2
@Component
@AllArgsConstructor
@KafkaListener(topics = "${kafka.aviation.account-emissions-updated-response.topic}",
		containerFactory = "emissionsAccountResponseAviationKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class AviationEmissionsUpdatedEventListener {

	private final AviationEmissionsUpdatedResponseHandler handler;

	@Transactional
	@KafkaHandler
	public void handle(@Payload AccountEmissionsUpdatedResponseEvent event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
		handler.handleResponse(event, correlationId);
	}
}
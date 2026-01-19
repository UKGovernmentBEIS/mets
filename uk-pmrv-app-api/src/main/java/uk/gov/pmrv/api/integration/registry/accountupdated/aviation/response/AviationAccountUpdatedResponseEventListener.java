package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.response;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.integration.model.account.AccountUpdatingEventOutcome;

@Log4j2
@Component
@AllArgsConstructor
@KafkaListener(topics = "${kafka.aviation.account-updated-response.topic}",
		containerFactory = "aviationAccountUpdatedKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.account.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountUpdatedResponseEventListener {

	private final AviationAccountUpdatedResponseHandler handler;

	@Transactional
	@KafkaHandler
	public void handle(@Payload AccountUpdatingEventOutcome event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
		handler.handleResponse(event, correlationId);
	}
}
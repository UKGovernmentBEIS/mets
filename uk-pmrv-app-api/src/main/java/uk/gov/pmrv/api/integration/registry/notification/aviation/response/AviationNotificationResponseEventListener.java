package uk.gov.pmrv.api.integration.registry.notification.aviation.response;

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
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.pmrv.api.integration.registry.notification.common.response.NotificationResponseHandler;
import uk.gov.pmrv.api.integration.registry.notification.common.response.RegistryNoticeDomain;

@Log4j2
@Component
@AllArgsConstructor
@KafkaListener(topics = "${kafka.aviation.notification-response.topic}",
		containerFactory = "aviationNotificationKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.notification.enabled", havingValue = "true", matchIfMissing = false)
public class AviationNotificationResponseEventListener {

	private final NotificationResponseHandler handler;

	@Transactional
	@KafkaHandler
	public void handle(@Payload RegulatorNoticeEventOutcome event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
		handler.handleResponse(event, correlationId, RegistryNoticeDomain.AVIATION);
	}
}
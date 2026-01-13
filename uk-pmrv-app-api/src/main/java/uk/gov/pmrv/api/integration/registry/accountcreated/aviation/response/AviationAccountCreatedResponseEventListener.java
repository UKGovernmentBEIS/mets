package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${kafka.aviation.account-created-response.topic}",
        containerFactory = "aviationAccountCreatedKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountCreatedResponseEventListener {

    private final AviationAccountCreatedResponseHandler handler;

    @KafkaHandler
    @Transactional
    public void handler(@Payload AccountOpeningEventOutcome event,
                        @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
        handler.handleResponse(event, correlationId);
    }
}

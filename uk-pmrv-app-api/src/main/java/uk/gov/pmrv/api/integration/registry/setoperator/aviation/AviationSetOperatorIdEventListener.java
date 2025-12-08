package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = "${kafka.aviation.operator-identifier-request.topic}",
        containerFactory = "aviationSetOperatorIdKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class AviationSetOperatorIdEventListener {

    private final AviationSetOperatorIdResponseHandler handler;

    @KafkaHandler
    @Transactional
    public void handler(@Payload OperatorUpdateEvent event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
        handler.handleResponse(event, correlationId);
    }


}

package uk.gov.pmrv.api.integration.registry.withholdflag.installation.response;

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
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEventOutcome;

@Log4j2
@Component
@AllArgsConstructor
@KafkaListener(topics = "${kafka.installation.withhold-flag-response.topic}",
        containerFactory = "installationWithholdFlagKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagResponseEventListener {

    private final InstallationWithholdFlagResponseHandler handler;

    @Transactional
    @KafkaHandler
    public void handle(@Payload AccountWithholdUpdateEventOutcome event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
        handler.handleResponse(event, correlationId);
    }
}

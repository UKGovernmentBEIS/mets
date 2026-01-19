package uk.gov.pmrv.api.integration.registry.accountcontacts.installation.response;

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
import uk.gov.netz.integration.model.metscontacts.MetsContactsEventOutcome;
import uk.gov.pmrv.api.integration.registry.accountcontacts.common.AccountContactResponseHandler;
import uk.gov.pmrv.api.integration.registry.common.NotifyRegistryUtils;

@Log4j2
@Component
@AllArgsConstructor
@KafkaListener(topics = "${kafka.installation.account-contact-response.topic}",
        containerFactory = "installationAccountContactKafkaListenerContainerFactory")
@ConditionalOnProperty(name = "registry.integration.account.contact.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationAccountContactResponseEventListener {

    private final AccountContactResponseHandler handler;

    @Transactional
    @KafkaHandler
    public void handle(@Payload MetsContactsEventOutcome event, @Header(KafkaConstants.CORRELATION_ID_HEADER) String correlationId) {
        handler.handleResponse(event, correlationId, NotifyRegistryUtils.INSTALLATION_SERVICE_KEY);
    }

}

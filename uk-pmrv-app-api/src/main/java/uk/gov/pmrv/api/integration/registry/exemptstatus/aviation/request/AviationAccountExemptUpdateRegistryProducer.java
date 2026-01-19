package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.aviation.exempt.update.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountExemptUpdateRegistryProducer {

    @Value("${kafka.aviation.account-exempt-update-request.topic}")
    private String topicName;

    private final KafkaTemplate<String, AccountExemptionUpdateEvent> aviationAccountExemptUpdateKafkaTemplate;

    @Transactional
    public void produce(AccountExemptionUpdateEvent event) {
        try {
            aviationAccountExemptUpdateKafkaTemplate.send(topicName, String.valueOf(event.getRegistryId()),event);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE,
                    event);
        }
    }
}

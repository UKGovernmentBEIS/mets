package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationWithholdFlagRegistryProducer {

    @Value("${kafka.installation.withhold-flag-request.topic}")
    private String topicName;

    private final KafkaTemplate<String, AccountWithholdUpdateEvent> withholdFlagKafkaTemplate;

    @Transactional
    public void produce(AccountWithholdUpdateEvent accountWithholdUpdateEvent) {
        try {
            withholdFlagKafkaTemplate.send(topicName, String.valueOf(accountWithholdUpdateEvent.getRegistryId()),accountWithholdUpdateEvent);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE,
                    accountWithholdUpdateEvent);
        }
    }

}

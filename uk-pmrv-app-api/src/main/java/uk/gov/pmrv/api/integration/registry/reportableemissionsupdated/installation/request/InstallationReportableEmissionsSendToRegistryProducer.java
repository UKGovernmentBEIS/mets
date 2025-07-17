package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.AccountEmissionsUpdatedRequestEvent;

@Log4j2
@Service
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationReportableEmissionsSendToRegistryProducer {

    private final String topicName;

    public InstallationReportableEmissionsSendToRegistryProducer(
        @Value("${kafka.installation.account-emissions-updated-request.topic}") String topicName) {
        this.topicName = topicName;
    }

    @Transactional
    public void produce(AccountEmissionsUpdatedRequestEvent event,
                        KafkaTemplate<String, AccountEmissionsUpdatedRequestEvent> kafkaTemplate) {
        try {
        	kafkaTemplate.send(topicName, String.valueOf(event.getRegistryId()), event);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_EMISSIONS_KAFKA_QUEUE_CONNECTION_ISSUE,
                event);
        }
    }
}
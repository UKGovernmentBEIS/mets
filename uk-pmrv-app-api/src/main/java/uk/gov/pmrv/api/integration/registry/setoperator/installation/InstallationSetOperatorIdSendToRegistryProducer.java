package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdEventOutcome;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true")
public class InstallationSetOperatorIdSendToRegistryProducer {

    @Value("${kafka.installation.operator-identifier-response.topic}")
    private String topicName;

    private final KafkaTemplate<String, SetOperatorIdEventOutcome> installationSetOperatorIdKafkaTemplate;

    @Transactional
    public void produce(SetOperatorIdEventOutcome eventOutcome) {
        try {
            installationSetOperatorIdKafkaTemplate.send(topicName, String.valueOf(eventOutcome.getEvent().getEmitterId()), eventOutcome);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_CREATE_KAFKA_QUEUE_CONNECTION_ISSUE,
                    eventOutcome);
        }
    }


}

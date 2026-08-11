package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.nio.charset.StandardCharsets;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true")
public class InstallationSetOperatorIdSendToRegistryProducer {

    @Value("${kafka.installation.operator-identifier-response.topic}")
    private String topicName;

    private final KafkaTemplate<String, OperatorUpdateEventOutcome> installationSetOperatorIdKafkaTemplate;

    @Transactional
    public void produce(OperatorUpdateEventOutcome eventOutcome, String correlationId, String parentCorrelationId) {
        try {
            ProducerRecord<String, OperatorUpdateEventOutcome> record = new ProducerRecord<>(
                    topicName, String.valueOf(eventOutcome.getEvent().getEmitterId()), eventOutcome);
            record.headers().add(KafkaConstants.CORRELATION_ID_HEADER, correlationId.getBytes(StandardCharsets.UTF_8));
            if (parentCorrelationId != null) {
                record.headers().add(KafkaConstants.CORRELATION_PARENT_ID_HEADER, parentCorrelationId.getBytes(StandardCharsets.UTF_8));
            }
            installationSetOperatorIdKafkaTemplate.send(record);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE,
                    eventOutcome);
        }
    }
}

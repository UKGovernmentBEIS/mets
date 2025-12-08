package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationEmpApprovedSendToRegistryProducer {

    @Value("${kafka.aviation.account-created-request.topic}")
    private String topicName;

    private final KafkaTemplate<String, AccountOpeningEvent> aviationAccountCreatedKafkaTemplate;

    @Transactional
    public void produce(AccountOpeningEvent accountOpeningEvent) {
        try {
            aviationAccountCreatedKafkaTemplate.send(topicName, String.valueOf(accountOpeningEvent.getAccountDetails().getEmitterId()), accountOpeningEvent);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE,
                    accountOpeningEvent);
        }
    }

}








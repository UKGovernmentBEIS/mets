package uk.gov.pmrv.api.integration.registry.accountcreated.installation.request;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationPermitGrantedSendToRegistryProducer {

    @Value("${kafka.installation.account-created-request.topic}")
    private String topicName;

    private final KafkaTemplate<String, InstallationAccountCreatedRegistryDTO> installationAccountCreatedKafkaTemplate;

    @Transactional
    public void produce(InstallationAccountCreatedRegistryDTO registryDTO) {
        try {
            installationAccountCreatedKafkaTemplate.send(topicName, String.valueOf(registryDTO.getAccountCreatedRegistryDetails().getEmitterId()), registryDTO);
        } catch (Exception e) {
            log.error("Error when kafka producing: {}", e.getMessage());
            throw new BusinessException(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_CREATE_KAFKA_QUEUE_CONNECTION_ISSUE,
                    registryDTO);
        }
    }


}

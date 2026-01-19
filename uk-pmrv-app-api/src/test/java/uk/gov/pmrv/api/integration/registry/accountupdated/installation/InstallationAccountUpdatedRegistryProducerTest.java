package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.account.AccountUpdatingEvent;
import uk.gov.netz.integration.model.account.UpdateAccountDetailsMessage;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryProducer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountUpdatedRegistryProducerTest {

    @Mock
    private KafkaTemplate<String, AccountUpdatingEvent> installationAccountUpdatedKafkaTemplate;

    @InjectMocks
    private InstallationAccountUpdatedRegistryProducer installationAccountUpdatedRegistryProducer;

    @Test
    void produce() {

        ReflectionTestUtils.setField(installationAccountUpdatedRegistryProducer, "topicName", "test-topic");

        AccountUpdatingEvent event = buildAccountUpdatingEvent();

        installationAccountUpdatedRegistryProducer.produce(event);

        verify(installationAccountUpdatedKafkaTemplate).send(eq("test-topic"), eq("123"), eq(event));
    }

    @Test
    void produce_kafka_exception() {

        ReflectionTestUtils.setField(installationAccountUpdatedRegistryProducer, "topicName", "test-topic");

        AccountUpdatingEvent event = buildAccountUpdatingEvent();

        doThrow(new RuntimeException("Kafka connection error"))
                .when(installationAccountUpdatedKafkaTemplate).send(any(), any(), any());

        BusinessException exception = assertThrows(BusinessException.class,
                () -> installationAccountUpdatedRegistryProducer.produce(event));

        assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE, exception.getErrorCode());
        verify(installationAccountUpdatedKafkaTemplate).send(eq("test-topic"), eq("123"), eq(event));
    }

    private AccountUpdatingEvent buildAccountUpdatingEvent() {
        UpdateAccountDetailsMessage accountDetails = UpdateAccountDetailsMessage.builder()
                .registryId("123")
                .installationName("Installation Name")
                .accountName("Account Name")
                .permitId("permitId")
                .build();

        return AccountUpdatingEvent.builder()
                .accountDetails(accountDetails)
                .build();
    }
}
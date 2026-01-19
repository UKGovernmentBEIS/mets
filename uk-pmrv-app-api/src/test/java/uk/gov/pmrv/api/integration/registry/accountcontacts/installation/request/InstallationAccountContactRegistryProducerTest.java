package uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallationAccountContactRegistryProducerTest {

    private static final String TOPIC_NAME = "topic";
    private static final String OPERATOR_ID = "OP12345";

    @Mock
    private KafkaTemplate<String, MetsContactsEvent> installationAccountContactKafkaTemplate;

    @InjectMocks
    private InstallationAccountContactRegistryProducer installationAccountContactRegistryProducer;

    @Test
    void produce_sends_message_successfully() {
        MetsContactsEvent metsContactsEvent = buildMetsContactsEvent();
        ReflectionTestUtils.setField(installationAccountContactRegistryProducer, "topicName", TOPIC_NAME);

        installationAccountContactRegistryProducer.produce(metsContactsEvent);

        verify(installationAccountContactKafkaTemplate).send(TOPIC_NAME, OPERATOR_ID, metsContactsEvent);
    }

    @Test
    void produce_throws_business_exception_on_failure() {
        MetsContactsEvent metsContactsEvent = buildMetsContactsEvent();
        ReflectionTestUtils.setField(installationAccountContactRegistryProducer, "topicName", TOPIC_NAME);

        doThrow(new RuntimeException("Kafka connection error"))
                .when(installationAccountContactKafkaTemplate).send(TOPIC_NAME, OPERATOR_ID, metsContactsEvent);

        BusinessException businessException = assertThrows(BusinessException.class,
                () -> installationAccountContactRegistryProducer.produce(metsContactsEvent));

        assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE, businessException.getErrorCode());
    }

    private MetsContactsEvent buildMetsContactsEvent() {
        return MetsContactsEvent.builder()
                .operatorId(OPERATOR_ID)
                .build();
    }
}
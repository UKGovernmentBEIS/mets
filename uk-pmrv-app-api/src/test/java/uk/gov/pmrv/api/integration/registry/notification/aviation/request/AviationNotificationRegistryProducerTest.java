package uk.gov.pmrv.api.integration.registry.notification.aviation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationNotificationRegistryProducerTest {

    private static final String TOPIC_NAME = "aviation-notification-topic";
    private static final String REGISTRY_ID = "12345";

    @Mock
    private KafkaTemplate<String, RegulatorNoticeEvent> aviationNoticeKafkaTemplate;

    @InjectMocks
    private AviationNotificationRegistryProducer producer;

    @Test
    void produce() {
        RegulatorNoticeEvent event = buildRegulatorNoticeEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        producer.produce(event);

        verify(aviationNoticeKafkaTemplate).send(TOPIC_NAME, REGISTRY_ID, event);
    }

    @Test
    void produce_throws_business_exception_on_error() {
        RegulatorNoticeEvent event = buildRegulatorNoticeEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        doThrow(new RuntimeException("Kafka error"))
                .when(aviationNoticeKafkaTemplate).send(TOPIC_NAME, REGISTRY_ID, event);

        BusinessException exception = assertThrows(BusinessException.class, () -> producer.produce(event));

        assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE, exception.getErrorCode());
        assertEquals(event, exception.getData()[0]);
    }

    private RegulatorNoticeEvent buildRegulatorNoticeEvent() {
        return RegulatorNoticeEvent.builder()
                .registryId(REGISTRY_ID)
                .type("Aviation account closed")
                .fileName("notice.pdf")
                .fileData("content".getBytes())
                .build();
    }
}
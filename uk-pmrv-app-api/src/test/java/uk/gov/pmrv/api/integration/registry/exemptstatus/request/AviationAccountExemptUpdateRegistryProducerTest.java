package uk.gov.pmrv.api.integration.registry.exemptstatus.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptUpdateRegistryProducer;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountExemptUpdateRegistryProducerTest {

    private static final String TOPIC_NAME = "aviation-exempt-topic";
    private static final Long REGISTRY_ID = 12345L;

    @Mock
    private KafkaTemplate<String, AccountExemptionUpdateEvent> aviationAccountExemptUpdateKafkaTemplate;

    @InjectMocks
    private AviationAccountExemptUpdateRegistryProducer producer;

    @Test
    void produce() {
        AccountExemptionUpdateEvent event = buildAccountExemptionUpdateEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        producer.produce(event);

        verify(aviationAccountExemptUpdateKafkaTemplate).send(TOPIC_NAME, String.valueOf(REGISTRY_ID), event);
    }

    @Test
    void produce_throws_business_exception_on_error() {
        AccountExemptionUpdateEvent event = buildAccountExemptionUpdateEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        doThrow(new RuntimeException("Kafka error"))
                .when(aviationAccountExemptUpdateKafkaTemplate).send(TOPIC_NAME, String.valueOf(REGISTRY_ID), event);

        BusinessException businessException = assertThrows(BusinessException.class, () -> producer.produce(event));

        assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE, businessException.getErrorCode());
        assertEquals(event, businessException.getData()[0]);
    }

    private AccountExemptionUpdateEvent buildAccountExemptionUpdateEvent() {
        AccountExemptionUpdateEvent event = new AccountExemptionUpdateEvent();
        event.setRegistryId(REGISTRY_ID);
        event.setReportingYear(Year.of(2023));
        event.setExemptionFlag(true);
        return event;
    }
}
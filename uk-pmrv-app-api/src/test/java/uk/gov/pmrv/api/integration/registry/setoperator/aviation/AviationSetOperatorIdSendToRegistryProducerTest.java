package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdEventOutcome;
import uk.gov.pmrv.api.integration.registry.setoperator.common.SetOperatorIdResponseEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationSetOperatorIdSendToRegistryProducerTest {

    @InjectMocks
    private AviationSetOperatorIdSendToRegistryProducer producer;

    @Mock
    private KafkaTemplate<String, SetOperatorIdEventOutcome> aviationSetOperatorIdKafkaTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "topicName", "test-topic");
    }

    @Test
    void produce_whenCalled_sendsToKafkaTemplate() {
        final String emitterId = "1";
        final SetOperatorIdEventOutcome eventOutcome = SetOperatorIdEventOutcome.builder()
                .event(SetOperatorIdResponseEvent.builder().emitterId(emitterId).build())
                .build();

        producer.produce(eventOutcome);

        verify(aviationSetOperatorIdKafkaTemplate, times(1))
                .send("test-topic", emitterId, eventOutcome);
    }

    @Test
    void produce_whenKafkaTemplateThrowsException_throwsBusinessException() {
        final String emitterId = "1";
        final SetOperatorIdEventOutcome eventOutcome = SetOperatorIdEventOutcome.builder()
                .event(SetOperatorIdResponseEvent.builder().emitterId(emitterId).build())
                .build();

        doThrow(new RuntimeException("Kafka connection error"))
                .when(aviationSetOperatorIdKafkaTemplate).send(any(), any(), any());

        BusinessException businessException = assertThrows(
                BusinessException.class,
                () -> producer.produce(eventOutcome)
        );

        assertThat(businessException.getErrorCode())
                .isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_CREATE_KAFKA_QUEUE_CONNECTION_ISSUE);
        assertThat(businessException.getData()).isEqualTo(new Object[]{eventOutcome});

        verify(aviationSetOperatorIdKafkaTemplate, times(1))
                .send("test-topic", String.valueOf(emitterId), eventOutcome);
    }
}
package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.kafka.utils.KafkaConstants;
import uk.gov.netz.integration.model.operator.OperatorUpdateEvent;
import uk.gov.netz.integration.model.operator.OperatorUpdateEventOutcome;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallationSetOperatorIdSendToRegistryProducerTest {

    private static final String CORRELATION_ID = "corr-id-B";
    private static final String PARENT_CORRELATION_ID = "parent-corr-id-O";

    @InjectMocks
    private InstallationSetOperatorIdSendToRegistryProducer producer;

    @Mock
    private KafkaTemplate<String, OperatorUpdateEventOutcome> installationSetOperatorIdKafkaTemplate;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producer, "topicName", "test-topic");
    }

    @Test
    void produce_whenCalled_sendsProducerRecordWithCorrelationHeaders() {
        final String emitterId = "1";
        final OperatorUpdateEventOutcome eventOutcome = OperatorUpdateEventOutcome.builder()
                .event(OperatorUpdateEvent.builder().emitterId(emitterId).build())
                .build();

        producer.produce(eventOutcome, CORRELATION_ID, PARENT_CORRELATION_ID);

        @SuppressWarnings("unchecked")
        final ArgumentCaptor<ProducerRecord<String, OperatorUpdateEventOutcome>> captor =
                ArgumentCaptor.forClass(ProducerRecord.class);
        verify(installationSetOperatorIdKafkaTemplate, times(1)).send(captor.capture());

        final ProducerRecord<String, OperatorUpdateEventOutcome> record = captor.getValue();
        assertThat(record.topic()).isEqualTo("test-topic");
        assertThat(record.key()).isEqualTo(emitterId);
        assertThat(record.value()).isEqualTo(eventOutcome);
        assertThat(new String(record.headers().lastHeader(KafkaConstants.CORRELATION_ID_HEADER).value(), StandardCharsets.UTF_8))
                .isEqualTo(CORRELATION_ID);
        assertThat(new String(record.headers().lastHeader(KafkaConstants.CORRELATION_PARENT_ID_HEADER).value(), StandardCharsets.UTF_8))
                .isEqualTo(PARENT_CORRELATION_ID);
    }

    @Test
    void produce_whenKafkaTemplateThrowsException_throwsBusinessException() {
        final OperatorUpdateEventOutcome eventOutcome = OperatorUpdateEventOutcome.builder()
                .event(OperatorUpdateEvent.builder().emitterId("2").build())
                .build();

        doThrow(new RuntimeException("Kafka connection error"))
                .when(installationSetOperatorIdKafkaTemplate).send(any(ProducerRecord.class));

        final BusinessException businessException = assertThrows(
                BusinessException.class,
                () -> producer.produce(eventOutcome, CORRELATION_ID, PARENT_CORRELATION_ID)
        );

        assertThat(businessException.getErrorCode())
                .isEqualTo(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE);
        assertThat(businessException.getData()).isEqualTo(new Object[]{eventOutcome});
    }
}

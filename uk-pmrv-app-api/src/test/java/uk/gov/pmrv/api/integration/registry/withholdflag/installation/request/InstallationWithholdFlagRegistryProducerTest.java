package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallationWithholdFlagRegistryProducerTest {

    private static final String TOPIC_NAME = "withhold-flag-topic";
    private static final Long REGISTRY_ID = 54321L;

    @Mock
    private KafkaTemplate<String, AccountWithholdUpdateEvent> withholdFlagKafkaTemplate;

    @InjectMocks
    private InstallationWithholdFlagRegistryProducer producer;

    @Test
    void produce() {
        AccountWithholdUpdateEvent event = buildAccountWithholdUpdateEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        producer.produce(event);

        verify(withholdFlagKafkaTemplate).send(TOPIC_NAME, String.valueOf(REGISTRY_ID), event);
    }

    @Test
    void produce_throws_business_exception_on_error() {
        AccountWithholdUpdateEvent event = buildAccountWithholdUpdateEvent();
        ReflectionTestUtils.setField(producer, "topicName", TOPIC_NAME);

        doThrow(new RuntimeException("Kafka connection failed"))
                .when(withholdFlagKafkaTemplate).send(TOPIC_NAME, String.valueOf(REGISTRY_ID), event);

        BusinessException businessException = assertThrows(BusinessException.class, () -> producer.produce(event));

        assertEquals(MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE, businessException.getErrorCode());
        assertEquals(event, businessException.getData()[0]);
    }

    private AccountWithholdUpdateEvent buildAccountWithholdUpdateEvent() {
        AccountWithholdUpdateEvent event = new AccountWithholdUpdateEvent();
        event.setRegistryId(REGISTRY_ID);
        event.setReportingYear(Year.of(2025));
        event.setWithholdFlag(true);
        return event;
    }
}
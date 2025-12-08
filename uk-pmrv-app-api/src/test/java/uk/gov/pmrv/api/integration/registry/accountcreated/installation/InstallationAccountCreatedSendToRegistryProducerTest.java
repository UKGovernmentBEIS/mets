package uk.gov.pmrv.api.integration.registry.accountcreated.installation;

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
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedSendToRegistryProducer;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountCreatedSendToRegistryProducerTest {

    @InjectMocks
    private InstallationAccountCreatedSendToRegistryProducer producer;

    private static final String TOPIC = "account-created-topic";

    @Mock
    private KafkaTemplate<String, AccountOpeningEvent> accountCreatedKafkaTemplate;

    @BeforeEach
    void injectTopicName() {
        ReflectionTestUtils.setField(producer, "topicName", TOPIC);
    }

    @Test
    public void produce() {

        AccountOpeningEvent event = mock(AccountOpeningEvent.class);
        AccountDetailsMessage details = mock(AccountDetailsMessage.class);
        when(event.getAccountDetails()).thenReturn(details);
        when(details.getEmitterId()).thenReturn("12345");

        producer.produce(event);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<AccountOpeningEvent> valueCaptor = ArgumentCaptor.forClass(AccountOpeningEvent.class);

        verify(accountCreatedKafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), valueCaptor.capture());
        verifyNoMoreInteractions(accountCreatedKafkaTemplate);

        assertEquals(TOPIC, topicCaptor.getValue(), "Topic name should match injected value");
        assertEquals("12345", keyCaptor.getValue(), "Key should be the emitterId");
        assertSame(event, valueCaptor.getValue());

    }

    @Test
    void produce_wrapsKafkaErrorsInBusinessException() {

        AccountOpeningEvent dto = mock(AccountOpeningEvent.class);
        AccountDetailsMessage details = mock(AccountDetailsMessage.class);
        when(dto.getAccountDetails()).thenReturn(details);
        when(details.getEmitterId()).thenReturn("12345");

        doThrow(new RuntimeException("broker down"))
                .when(accountCreatedKafkaTemplate)
                .send(anyString(), anyString(), any(AccountOpeningEvent.class));

        BusinessException ex = assertThrows(BusinessException.class, () -> producer.produce(dto));

        assertEquals(
                MetsErrorCode.INTEGRATION_REGISTRY_ACCOUNT_KAFKA_QUEUE_CONNECTION_ISSUE,
                ex.getErrorCode(),
                "Error code should match mapping on failure"
        );

        verify(accountCreatedKafkaTemplate).send(eq(TOPIC), eq("12345"), eq(dto));
        verifyNoMoreInteractions(accountCreatedKafkaTemplate);
    }


}

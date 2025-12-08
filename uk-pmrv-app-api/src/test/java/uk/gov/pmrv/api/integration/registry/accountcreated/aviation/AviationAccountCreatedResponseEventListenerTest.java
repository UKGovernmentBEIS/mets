package uk.gov.pmrv.api.integration.registry.accountcreated.aviation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.account.AccountDetailsMessage;
import uk.gov.netz.integration.model.account.AccountOpeningEvent;
import uk.gov.netz.integration.model.account.AccountOpeningEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response.AviationAccountCreatedResponseEventListener;
import uk.gov.pmrv.api.integration.registry.accountcreated.aviation.response.AviationAccountCreatedResponseHandler;

import java.util.List;

public class AviationAccountCreatedResponseEventListenerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-123";
    private static final Integer TEST_REGISTRY_ID = 1234;

    @Mock
    private AviationAccountCreatedResponseHandler handler;

    @InjectMocks
    private AviationAccountCreatedResponseEventListener listener;

    private AccountOpeningEventOutcome mockEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        AccountDetailsMessage accountCreatedRegistryDetails = AccountDetailsMessage.builder()
                .emitterId("1")
                .accountName("name")
                .build();

        mockEvent = AccountOpeningEventOutcome.builder()
                .event(AccountOpeningEvent.builder()
                        .accountDetails(accountCreatedRegistryDetails)
                        .build())
                .outcome(IntegrationEventOutcome.ERROR)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0111)
                        .errorMessage("errorMessage")
                        .build())
                )
                .build();
    }

    @Test
    void testHandleEvent_Success() {
        listener.handler(mockEvent, TEST_CORRELATION_ID);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, TEST_CORRELATION_ID);
    }

    @Test
    void testHandleEvent_NullCorrelationId() {
        listener.handler(mockEvent, null);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, null);
    }
}

package uk.gov.pmrv.api.integration.registry.exemptstatus.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.error.IntegrationEventError;
import uk.gov.netz.integration.model.error.IntegrationEventErrorDetails;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEvent;
import uk.gov.netz.integration.model.exemption.AccountExemptionUpdateEventOutcome;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response.AviationAccountExemptResponseEventListener;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response.AviationAccountExemptResponseHandler;

import java.time.Year;
import java.util.List;

public class AviationAccountExemptResponseEventListenerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-123";
    private static final Long TEST_REGISTRY_ID = 1234L;

    @Mock
    private AviationAccountExemptResponseHandler handler;

    @InjectMocks
    private AviationAccountExemptResponseEventListener listener;

    private AccountExemptionUpdateEventOutcome mockEvent;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        AccountExemptionUpdateEvent aviationEvent = AccountExemptionUpdateEvent.builder()
                .registryId(TEST_REGISTRY_ID)
                .exemptionFlag(true)
                .reportingYear(Year.of(2025))
                .build();

        mockEvent = AccountExemptionUpdateEventOutcome.builder()
                .event(aviationEvent)
                .outcome(IntegrationEventOutcome.ERROR)
                .errors(List.of(IntegrationEventErrorDetails.builder()
                        .error(IntegrationEventError.ERROR_0400)
                        .errorMessage("errorMessage")
                        .build())
                )
                .build();
    }

    @Test
    void testHandleEvent_Success() {
        listener.handle(mockEvent, TEST_CORRELATION_ID);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, TEST_CORRELATION_ID);
    }

    @Test
    void testHandleEvent_NullCorrelationId() {
        listener.handle(mockEvent, null);
        Mockito.verify(handler, Mockito.times(1))
                .handleResponse(mockEvent, null);
    }
}

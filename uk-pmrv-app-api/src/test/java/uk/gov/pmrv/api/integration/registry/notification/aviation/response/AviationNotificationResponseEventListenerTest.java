package uk.gov.pmrv.api.integration.registry.notification.aviation.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.IntegrationEventOutcome;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEvent;
import uk.gov.netz.integration.model.regulatornotice.RegulatorNoticeEventOutcome;
import uk.gov.pmrv.api.integration.registry.notification.common.response.NotificationResponseHandler;
import uk.gov.pmrv.api.integration.registry.notification.common.response.RegistryNoticeDomain;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationNotificationResponseEventListenerTest {

    private static final String TEST_CORRELATION_ID = "test-correlation-123";

    @Mock
    private NotificationResponseHandler handler;

    @InjectMocks
    private AviationNotificationResponseEventListener listener;

    @Test
    void handle_shouldDelegateToHandlerWithAviationDomain() {
        RegulatorNoticeEventOutcome event = buildEvent();

        listener.handle(event, TEST_CORRELATION_ID);

        verify(handler, times(1))
                .handleResponse(event, TEST_CORRELATION_ID, RegistryNoticeDomain.AVIATION);
    }

    @Test
    void handle_shouldDelegateToHandlerWithNullCorrelationId() {
        RegulatorNoticeEventOutcome event = buildEvent();

        listener.handle(event, null);

        verify(handler, times(1))
                .handleResponse(event, null, RegistryNoticeDomain.AVIATION);
    }

    private static RegulatorNoticeEventOutcome buildEvent() {
        return RegulatorNoticeEventOutcome.builder()
                .event(RegulatorNoticeEvent.builder()
                        .registryId("1234")
                        .build())
                .outcome(IntegrationEventOutcome.ERROR)
                .build();
    }
}
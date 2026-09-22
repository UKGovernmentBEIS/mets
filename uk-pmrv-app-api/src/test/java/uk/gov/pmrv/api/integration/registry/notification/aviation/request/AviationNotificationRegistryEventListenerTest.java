package uk.gov.pmrv.api.integration.registry.notification.aviation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.notification.common.RegistryNotificationType;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationNotificationRegistryEventListenerTest {

    @Mock
    private AviationNotificationNotifyRegistryService aviationNotificationNotifyRegistryService;

    @InjectMocks
    private AviationNotificationRegistryEventListener listener;

    @Test
    void handleNotificationRegistryEvent_delegates_to_service_with_event() {
        AviationNotificationRegistryEvent event = AviationNotificationRegistryEvent.builder()
                .accountId(1L)
                .requestId("REQ-1")
                .registryNotificationType(RegistryNotificationType.AVIATION_ACCOUNT_CLOSED)
                .build();

        listener.handleNotificationRegistryEvent(event);

        verify(aviationNotificationNotifyRegistryService, times(1)).notifyRegistry(event);
    }
}
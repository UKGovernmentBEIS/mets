package uk.gov.pmrv.api.integration.registry.accountcreated.installation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.PermitGrantedEventListener;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.PermitGrantedNotifyRegistryService;
import uk.gov.pmrv.api.permit.domain.event.PermitGrantedEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PermitGrantedEventListenerTest {

    @Mock
    private PermitGrantedNotifyRegistryService permitGrantedNotifyRegistryService;

    private PermitGrantedEventListener permitGrantedEventListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        permitGrantedEventListener = new PermitGrantedEventListener(permitGrantedNotifyRegistryService);
    }

    @Test
    public void testPermitGrantedEvent() {
        PermitGrantedEvent permitGrantedEvent = PermitGrantedEvent.builder().accountId(1L)
                .requestId("request1").build();

        permitGrantedEventListener.handle(permitGrantedEvent);
        verify(permitGrantedNotifyRegistryService, times(1)).notifyRegistry(permitGrantedEvent);
    }
}

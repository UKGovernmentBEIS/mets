package uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AviationAccountUpdatedEventListenerTest {

    @Mock
    private AviationAccountUpdatedNotifyRegistryService notifyRegistryService;

    @InjectMocks
    private AviationAccountUpdatedEventListener aviationAccountUpdatedEventListener;

    @Test
    void handle() {

        AviationAccountUpdatedRegistryEvent event = buildAviationAccountUpdatedRegistryEvent();

        aviationAccountUpdatedEventListener.handle(event);

        verify(notifyRegistryService).notifyRegistry(event);
    }

    private AviationAccountUpdatedRegistryEvent buildAviationAccountUpdatedRegistryEvent() {
        return AviationAccountUpdatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("requestId")
                .build();
    }
}
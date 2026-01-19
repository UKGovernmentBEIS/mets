package uk.gov.pmrv.api.integration.registry.exemptstatus.request;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptUpdateRegistryEventListener;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptUpdateNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.request.AviationAccountExemptFlagEvent;

import java.time.Year;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountExemptUpdateRegistryEventListenerTest {

    @Mock
    private AviationAccountExemptUpdateNotifyRegistryService aviationAccountExemptUpdateNotifyRegistryService;

    @InjectMocks
    private AviationAccountExemptUpdateRegistryEventListener listener;

    @Test
    void handleAccountContactRegistryEvent() {
        AviationAccountExemptFlagEvent event = AviationAccountExemptFlagEvent.builder()
                .accountId(1L)
                .registryId(123)
                .year(Year.of(2026))
                .isExempt(true)
                .build();

        listener.handleAccountContactRegistryEvent(event);

        verify(aviationAccountExemptUpdateNotifyRegistryService, times(1)).notifyRegistry(event);
    }
}
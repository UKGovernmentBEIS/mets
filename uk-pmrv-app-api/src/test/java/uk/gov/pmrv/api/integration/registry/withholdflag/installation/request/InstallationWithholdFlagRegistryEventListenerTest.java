package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class InstallationWithholdFlagRegistryEventListenerTest {

    @Mock
    private InstallationWithholdFlagNotifyRegistryService installationWithholdFlagNotifyRegistryService;

    @InjectMocks
    private InstallationWithholdFlagRegistryEventListener listener;

    @Test
    void handleWithholdFlagRegistryEvent() {
        WithholdFlagRegistryEvent event = WithholdFlagRegistryEvent.builder()
                .accountId(1L)
                .year(2025)
                .withholdFlag(true)
                .build();

        listener.handleWithholdFlagRegistryEvent(event);

        verify(installationWithholdFlagNotifyRegistryService, times(1)).notifyRegistry(event);
    }
}
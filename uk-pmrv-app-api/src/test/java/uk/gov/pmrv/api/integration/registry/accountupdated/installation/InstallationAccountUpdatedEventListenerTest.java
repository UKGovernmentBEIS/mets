package uk.gov.pmrv.api.integration.registry.accountupdated.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedEventListener;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InstallationAccountUpdatedEventListenerTest {

    @Mock
    private InstallationAccountUpdatedNotifyRegistryService notifyRegistryService;

    @InjectMocks
    private InstallationAccountUpdatedEventListener installationAccountUpdatedEventListener;

    @Test
    void handle() {

        InstallationAccountUpdatedRegistryEvent event = buildInstallationAccountUpdatedRegistryEvent();

        installationAccountUpdatedEventListener.handle(event);

        verify(notifyRegistryService).notifyRegistry(event);
    }

    private InstallationAccountUpdatedRegistryEvent buildInstallationAccountUpdatedRegistryEvent() {
        return InstallationAccountUpdatedRegistryEvent.builder()
                .accountId(1L)
                .requestId("requestId")
                .build();
    }
}
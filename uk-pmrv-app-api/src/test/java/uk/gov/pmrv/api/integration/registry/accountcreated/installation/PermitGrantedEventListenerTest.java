package uk.gov.pmrv.api.integration.registry.accountcreated.installation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedEventListener;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcreated.installation.request.InstallationAccountCreatedRegistryEvent;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PermitGrantedEventListenerTest {

    @Mock
    private InstallationAccountCreatedNotifyRegistryService installationAccountCreatedNotifyRegistryService;

    private InstallationAccountCreatedEventListener installationAccountCreatedEventListener;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        installationAccountCreatedEventListener = new InstallationAccountCreatedEventListener(installationAccountCreatedNotifyRegistryService);
    }

    @Test
    public void testPermitGrantedEvent() {
        InstallationAccountCreatedRegistryEvent permitGrantedEvent = InstallationAccountCreatedRegistryEvent.builder().accountId(1L)
                .requestId("request1").build();

        installationAccountCreatedEventListener.handle(permitGrantedEvent);
        verify(installationAccountCreatedNotifyRegistryService, times(1)).notifyRegistry(permitGrantedEvent);
    }
}

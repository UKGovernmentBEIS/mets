package uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.integration.registry.accountcontacts.common.AccountContactRegistryIntegrationUtilityService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountContactNotifyRegistryServiceTest {

    private static final Integer REGISTRY_ID = 1;
    private static final String OPERATOR_ID = "1";

    @Mock
    private AccountContactRegistryIntegrationUtilityService utilityService;

    @Mock
    private InstallationAccountContactRegistryProducer registryProducer;

    @InjectMocks
    private InstallationAccountContactNotifyRegistryService installationAccountContactNotifyRegistryService;

    @Test
    void notifyRegistry_sends_event_when_registry_id_is_present() {
        Account account = buildAccount(REGISTRY_ID);
        MetsContactsEvent metsContactsEvent = buildMetsContactsEvent();

        when(utilityService.buildMetsContactsEvent(account)).thenReturn(metsContactsEvent);

        installationAccountContactNotifyRegistryService.notifyRegistry(account);

        verify(utilityService).buildMetsContactsEvent(account);
        verify(registryProducer).produce(metsContactsEvent);
    }

    @Test
    void notifyRegistry_does_not_send_event_when_registry_id_is_null() {
        Account account = buildAccount(null);

        installationAccountContactNotifyRegistryService.notifyRegistry(account);

        verifyNoInteractions(utilityService, registryProducer);
    }


    private Account buildAccount(Integer registryId) {
        return InstallationAccount.builder().registryId(registryId).build();
    }

    private MetsContactsEvent buildMetsContactsEvent() {
        return MetsContactsEvent.builder()
                .operatorId(InstallationAccountContactNotifyRegistryServiceTest.OPERATOR_ID)
                .build();
    }
}
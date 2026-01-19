package uk.gov.pmrv.api.integration.registry.accountcontacts.aviation.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.integration.model.metscontacts.MetsContactsEvent;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.integration.registry.accountcontacts.common.AccountContactRegistryIntegrationUtilityService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountContactNotifyRegistryServiceTest {

    private static final Integer REGISTRY_ID = 1;
    private static final String OPERATOR_ID = "1";

    @Mock
    private AccountContactRegistryIntegrationUtilityService utilityService;

    @Mock
    private AviationAccountContactRegistryProducer registryProducer;

    @InjectMocks
    private AviationAccountContactNotifyRegistryService aviationAccountContactNotifyRegistryService;

    @Test
    void notifyRegistry_sends_event_when_registry_id_is_present() {
        Account account = buildAccount(REGISTRY_ID);
        MetsContactsEvent metsContactsEvent = buildMetsContactsEvent();

        when(utilityService.buildMetsContactsEvent(account)).thenReturn(metsContactsEvent);

        aviationAccountContactNotifyRegistryService.notifyRegistry(account);

        verify(utilityService).buildMetsContactsEvent(account);
        verify(registryProducer).produce(metsContactsEvent);
    }

    @Test
    void notifyRegistry_does_not_send_event_when_registry_id_is_null() {
        Account account = buildAccount(null);

        aviationAccountContactNotifyRegistryService.notifyRegistry(account);

        verifyNoInteractions(utilityService, registryProducer);
    }


    private Account buildAccount(Integer registryId) {
        return AviationAccount.builder().registryId(registryId).build();
    }

    private MetsContactsEvent buildMetsContactsEvent() {
        return MetsContactsEvent.builder()
                .operatorId(AviationAccountContactNotifyRegistryServiceTest.OPERATOR_ID)
                .build();
    }
}
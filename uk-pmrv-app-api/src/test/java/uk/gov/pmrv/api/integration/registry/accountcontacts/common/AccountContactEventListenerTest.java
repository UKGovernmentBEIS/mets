package uk.gov.pmrv.api.integration.registry.accountcontacts.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.event.AccountContactRegistryEvent;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.integration.registry.accountcontacts.aviation.request.AviationAccountContactNotifyRegistryService;
import uk.gov.pmrv.api.integration.registry.accountcontacts.installation.request.InstallationAccountContactNotifyRegistryService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactEventListenerTest {

    private static final Long ACCOUNT_ID = 1L;

    @Mock
    private InstallationAccountContactNotifyRegistryService installationAccountContactNotifyRegistryService;

    @Mock
    private AviationAccountContactNotifyRegistryService aviationAccountContactNotifyRegistryService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountContactEventListener accountContactEventListener;

    @Test
    void handleAccountContactRegistryEvent_for_installation_ukets() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEvent();
        Account account = buildAccount(ACCOUNT_ID,AccountType.INSTALLATION, EmissionTradingScheme.UK_ETS_INSTALLATIONS);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountContactEventListener.handleAccountContactRegistryEvent(event);

        verify(installationAccountContactNotifyRegistryService).notifyRegistry(account);
        verifyNoInteractions(aviationAccountContactNotifyRegistryService);
    }

    @Test
    void handleAccountContactRegistryEvent_for_aviation_ukets() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEvent();
        Account account = buildAccount(ACCOUNT_ID,AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountContactEventListener.handleAccountContactRegistryEvent(event);

        verify(aviationAccountContactNotifyRegistryService).notifyRegistry(account);
        verifyNoInteractions(installationAccountContactNotifyRegistryService);
    }

    @Test
    void handleAccountContactRegistryEvent_for_account_not_found() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEvent();

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class,
                () -> accountContactEventListener.handleAccountContactRegistryEvent(event));

        verifyNoInteractions(installationAccountContactNotifyRegistryService);
        verifyNoInteractions(aviationAccountContactNotifyRegistryService);
    }

    @Test
    void handleAccountContactRegistryEvent_for_installation_but_not_ukets() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEvent();
        Account account = buildAccount(ACCOUNT_ID,AccountType.INSTALLATION, EmissionTradingScheme.EU_ETS_INSTALLATIONS);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountContactEventListener.handleAccountContactRegistryEvent(event);

        verify(installationAccountContactNotifyRegistryService, never()).notifyRegistry(account);
        verifyNoInteractions(aviationAccountContactNotifyRegistryService);
    }

    @Test
    void handleAccountContactRegistryEvent_for_aviation_but_not_ukets() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEvent();
        Account account = buildAccount(ACCOUNT_ID,AccountType.AVIATION, EmissionTradingScheme.CORSIA);

        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));

        accountContactEventListener.handleAccountContactRegistryEvent(event);

        verify(aviationAccountContactNotifyRegistryService, never()).notifyRegistry(account);
        verifyNoInteractions(installationAccountContactNotifyRegistryService);
    }

    @Test
    void handleAccountContactRegistryEvent_for_aviation_multiple_accounts() {
        AccountContactRegistryEvent event = buildAccountContactRegistryEventMultiple();
        Account account1 = buildAccount(1L,AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION);
        Account account2 = buildAccount(2L,AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION);
        Account account3 = buildAccount(3L,AccountType.AVIATION, EmissionTradingScheme.UK_ETS_AVIATION);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(account2));
        when(accountRepository.findById(3L)).thenReturn(Optional.of(account3));
        accountContactEventListener.handleAccountContactRegistryEvent(event);
        verify(aviationAccountContactNotifyRegistryService,times(3)).notifyRegistry(any());
    }

    private AccountContactRegistryEvent buildAccountContactRegistryEvent() {
        return AccountContactRegistryEvent.builder()
                .accountsIds(List.of(ACCOUNT_ID))
                .build();
    }

    private AccountContactRegistryEvent buildAccountContactRegistryEventMultiple() {
        return AccountContactRegistryEvent.builder()
                .accountsIds(List.of(1L, 2L, 3L))
                .build();
    }

    private Account buildAccount(Long accountId, AccountType accountType, EmissionTradingScheme scheme) {
        if(AccountType.INSTALLATION.equals(accountType)) {
            return InstallationAccount.builder()
                    .id(accountId)
                    .accountType(accountType)
                    .emissionTradingScheme(scheme)
                    .build();
        }
        else {
            return AviationAccount.builder()
                    .id(ACCOUNT_ID)
                    .accountType(accountType)
                    .emissionTradingScheme(scheme)
                    .build();
        }

    }
}
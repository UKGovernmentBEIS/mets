package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeDeleteValidator;
import uk.gov.pmrv.api.account.service.validator.PrimaryContactValidator;
import uk.gov.netz.api.common.exception.BusinessException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactDeletionServiceTest {

    @InjectMocks
    private AccountContactDeletionService accountContactDeletionService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PrimaryContactValidator primaryContactValidator;

    @Spy
    private ArrayList<AccountContactTypeDeleteValidator> accountContactTypeDeleteValidators;

    @BeforeEach
    void setUp() {
        accountContactTypeDeleteValidators.add(primaryContactValidator);
    }

    @Test
    void removeUserFromAccountContacts() {
        Long accountId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";

        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.PRIMARY, userId2);
        accountContacts.put(AccountContactType.SECONDARY, userId1);

        Account account = Mockito.mock(Account.class);
        when(account.getContacts()).thenReturn(accountContacts);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountContactDeletionService.removeUserFromAccountContacts(userId1, accountId);

        assertEquals(account.getName(), account.getName());
        assertFalse(account.getContacts().containsValue(userId1));
        assertThat(account.getContacts()).containsOnlyKeys(AccountContactType.PRIMARY);
    }

    @Test
    void removeUserFromAccountContacts_when_user_not_related_to_account_contacts() {
        Long accountId = 1L;
        String userId1 = "userId1";
        String userId2 = "userId2";

        Map<AccountContactType, String> accountContacts = new HashMap<>();
        accountContacts.put(AccountContactType.PRIMARY, userId2);
        accountContacts.put(AccountContactType.SECONDARY, userId2);

        Account account = Mockito.mock(Account.class);
        when(account.getContacts()).thenReturn(accountContacts);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        accountContactDeletionService.removeUserFromAccountContacts(userId1, accountId);

        verify(accountRepository, never()).save(any());
    }

    @Test
    void removeUserFromAccountContacts_throws_exception_when_account_not_found() {
        Long accountId = 1L;
        String userId = "userId";

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> accountContactDeletionService.removeUserFromAccountContacts(userId, accountId));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }
}
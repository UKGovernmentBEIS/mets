package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.AuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAuthService;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactQueryServiceTest {

    @InjectMocks
    private AccountContactQueryService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AuthorityService authorityService;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Test
    void findContactByAccountAndContactType() {
        Long accountId = 1L;
        String caSiteContact = "ca";
        Account account = Mockito.mock(Account.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(Map.of(AccountContactType.CA_SITE, caSiteContact));

        Optional<String> caSiteContactOpt = service.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);

        assertThat(caSiteContactOpt).contains(caSiteContact);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactByAccountAndContactType_no_contact_found() {
        Long accountId = 1L;
        Account account = Mockito.mock(Account.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(Map.of(AccountContactType.FINANCIAL, "financial"));

        Optional<String> caSiteContactOpt = service.findContactByAccountAndContactType(accountId, AccountContactType.CA_SITE);

        assertThat(caSiteContactOpt).isEmpty();
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findPrimaryContactByAccount() {
        Long accountId = 1L;
        String primaryContact = "user";
        Account account = Mockito.mock(Account.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(Map.of(AccountContactType.PRIMARY, primaryContact));

        Optional<String> resultOptional = service.findPrimaryContactByAccount(accountId);
        assertThat(resultOptional).isNotEmpty();
        assertEquals(primaryContact, resultOptional.get());
    }

    @Test
    void findPrimaryContactByAccount_not_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        assertThat(service.findPrimaryContactByAccount(accountId)).isEmpty();
    }

    @Test
    void findContactTypesByAccount() {
        Long accountId = 1L;

        Map<AccountContactType, String> contactTypes =
                Map.of(
                        AccountContactType.PRIMARY, "primary",
                        AccountContactType.SERVICE, "service");

        Account account = Mockito.mock(Account.class);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(contactTypes);

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(contactTypes);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactTypesByAccount_account_not_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findContactTypesByAccount_empty_contacts() {
        Long accountId = 1L;
        Account account = Mockito.mock(Account.class);
        Map<AccountContactType, String> contactTypes = Map.of();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(contactTypes);

        //invoke
        Map<AccountContactType, String> result = service.findContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void findOperatorContactTypesByAccount() {
        Long accountId = 1L;
        Account account = Mockito.mock(Account.class);

        Map<AccountContactType, String> contactTypes =
                Map.of(AccountContactType.PRIMARY, "primary",
                        AccountContactType.SERVICE, "service",
                        AccountContactType.SECONDARY, "secondary",
                        AccountContactType.FINANCIAL, "financial",
                        AccountContactType.CA_SITE, "ca_site",
                        AccountContactType.VB_SITE, "vb_site");

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(account.getContacts()).thenReturn(contactTypes);

        Map<AccountContactType, String> result = service.findOperatorContactTypesByAccount(accountId);

        assertThat(result).isEqualTo(Map.of(AccountContactType.PRIMARY, "primary",
                AccountContactType.SECONDARY, "secondary",
                AccountContactType.SERVICE, "service",
                AccountContactType.FINANCIAL, "financial"));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getServiceContactDetails() {
        Long accountId = 1L;
        String primaryContact = "primaryContactId";
        String serviceContact = "serviceContactId";
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(AviationAccount.builder()
                .id(accountId)
                .contacts(Map.of(AccountContactType.PRIMARY, primaryContact, AccountContactType.SERVICE, serviceContact))
                .build()));
        when(authorityService.findAuthorityByUserIdAndAccountId(serviceContact, accountId)).thenReturn(Optional.of(
                AuthorityInfoDTO.builder()
                        .code("roleCode")
                        .build()));
        when(operatorUserAuthService.getOperatorUserById(serviceContact)).thenReturn(OperatorUserDTO.builder()
                .firstName("fname")
                .lastName("lname")
                .email("email")
                .build());
        final Optional<ServiceContactDetails> actual = service.getServiceContactDetails(accountId);
        verify(accountRepository, times(1)).findById(accountId);
        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(serviceContact, accountId);
        verify(operatorUserAuthService, times(1)).getOperatorUserById(serviceContact);
        assertNotNull(actual.get());
        assertEquals("fname lname", actual.get().getName());
        assertEquals("roleCode", actual.get().getRoleCode());
        assertEquals("email", actual.get().getEmail());
    }

    @Test
    void getServiceContactDetails_throws_exception() {
        Long accountId = 1L;
        String primaryContact = "primaryContactId";
        String serviceContact = "serviceContactId";
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(AviationAccount.builder()
                .id(accountId)
                .contacts(Map.of(AccountContactType.PRIMARY, primaryContact, AccountContactType.SERVICE, serviceContact))
                .build()));
        when(authorityService.findAuthorityByUserIdAndAccountId(serviceContact, accountId)).thenReturn(Optional.empty());
        BusinessException be = assertThrows(BusinessException.class,
                () -> service.getServiceContactDetails(accountId));
        verify(accountRepository, times(1)).findById(accountId);
        verify(authorityService, times(1)).findAuthorityByUserIdAndAccountId(serviceContact, accountId);
        verifyNoInteractions(operatorUserAuthService);
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
    }

    @Test
    void getServiceContactDetails_account_not_exist() {
        Long accountId = 1L;
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        final Optional<ServiceContactDetails> actual = service.getServiceContactDetails(accountId);
        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(authorityService);
        verifyNoInteractions(operatorUserAuthService);
        assertThat(actual).isEmpty();
    }

    @Test
    void getServiceContactDetails_account_service_contact_not_exist() {
        Long accountId = 1L;
        String primaryContact = "primaryContactId";
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(AviationAccount.builder()
                        .id(accountId)
                        .contacts(Map.of(AccountContactType.PRIMARY, primaryContact))
                .build()));
        final Optional<ServiceContactDetails> actual = service.getServiceContactDetails(accountId);
        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(authorityService);
        verifyNoInteractions(operatorUserAuthService);
        assertThat(actual).isEmpty();
    }
}

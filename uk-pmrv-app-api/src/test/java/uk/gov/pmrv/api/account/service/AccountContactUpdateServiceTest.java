package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.aviation.domain.dto.ServiceContactDetails;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.event.FirstPrimaryContactAssignedToAccountEvent;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.validator.AccountContactTypeUpdateValidator;
import uk.gov.pmrv.api.account.service.validator.FinancialContactValidator;
import uk.gov.pmrv.api.account.service.validator.PrimaryContactValidator;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.service.AuthorityService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountContactUpdateServiceTest {

    @InjectMocks
    private AccountContactUpdateService service;
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private AuthorityService authorityService;
    
    @Mock
    private FinancialContactValidator financialContactValidator;

    @Mock
    private PrimaryContactValidator primaryContactValidator;
    
    @Spy
    private ArrayList<AccountContactTypeUpdateValidator> contactTypeValidators;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @BeforeEach
    void setUp() {
        contactTypeValidators.add(financialContactValidator);
        contactTypeValidators.add(primaryContactValidator);
    }
    
    @Test
    void assignUserAsDefaultAccountContactPoint() {
        String user = "user";
        Map<AccountContactType, String> contacts = new EnumMap<>(AccountContactType.class);

        Account account = Mockito.mock(Account.class);
        when(account.getContacts()).thenReturn(contacts);
        
        assertThat(account.getContacts()).isEmpty();
        
        service.assignUserAsDefaultAccountContactPoint(user, account);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getContacts()).containsEntry(AccountContactType.PRIMARY, user);
        assertThat(accountCaptured.getContacts()).containsEntry(AccountContactType.SERVICE, user);
        assertThat(accountCaptured.getContacts()).containsEntry(AccountContactType.FINANCIAL, user);
    }
    
    @Test
    void updateAccountContacts() {
        Long accountId = 1L;
        Map<AccountContactType, String> currentAccountContacts = 
                new EnumMap<>(AccountContactType.class);
        currentAccountContacts.put(AccountContactType.PRIMARY, "primaryCurrent");
        currentAccountContacts.put(AccountContactType.FINANCIAL, "financialCurrent");
        currentAccountContacts.put(AccountContactType.SERVICE, "serviceCurrent");
        currentAccountContacts.put(AccountContactType.SECONDARY, "secondaryCurrent");
        currentAccountContacts.put(AccountContactType.CA_SITE, "regulator");

        Account account = Mockito.mock(Account.class);
        when(account.getContacts()).thenReturn(currentAccountContacts);
        
        Map<AccountContactType, String> updatedContactTypes = 
                new EnumMap<>(AccountContactType.class);
        updatedContactTypes.put(AccountContactType.PRIMARY, "primaryNew");
        updatedContactTypes.put(AccountContactType.FINANCIAL, "financialDisabled");
        updatedContactTypes.put(AccountContactType.SECONDARY, null);

        Map<String, AuthorityStatus> operatorStatuses =
            Map.of(
                "primaryNew", AuthorityStatus.ACTIVE,
                "financialDisabled", AuthorityStatus.DISABLED,
                "serviceCurrent", AuthorityStatus.ACTIVE
            );
        
        Map<String, AuthorityStatus> regulatorStatus = Map.of("regulator", AuthorityStatus.ACTIVE);

        List<String> finalUsers = List.of("primaryNew", "serviceCurrent", "financialDisabled");
        
        when(authorityService.existsByUserIdAndAccountId("primaryNew", accountId)).thenReturn(true);
        when(authorityService.existsByUserIdAndAccountId("financialDisabled", accountId)).thenReturn(true);
        when(accountRepository.findById(accountId)).thenReturn(
                Optional.of(account));
        when(authorityService.findStatusByUsersAndAccountId(finalUsers, accountId)).thenReturn(operatorStatuses);
        when(authorityService.findStatusByUsers(List.of("regulator"))).thenReturn(regulatorStatus);
        
        //invoke
        service.updateAccountContacts(updatedContactTypes, accountId);

        Map<AccountContactType, String> expectedContactTypes =
                new EnumMap<>(AccountContactType.class);
        expectedContactTypes.put(AccountContactType.PRIMARY, "primaryNew");
        expectedContactTypes.put(AccountContactType.FINANCIAL, null);
        expectedContactTypes.put(AccountContactType.SECONDARY, null);
        expectedContactTypes.put(AccountContactType.SERVICE, "serviceCurrent");
        expectedContactTypes.put(AccountContactType.CA_SITE, "regulator");

        verify(authorityService, times(1)).existsByUserIdAndAccountId("primaryNew", accountId);
        verify(authorityService, times(1)).existsByUserIdAndAccountId("financialDisabled", accountId);
        verifyNoMoreInteractions(authorityService);
        verify(financialContactValidator, times(1)).validateUpdate(expectedContactTypes, accountId);
        verify(primaryContactValidator, times(1)).validateUpdate(expectedContactTypes, accountId);
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void updateAccountContacts_no_current_contacts_exist() {
        Long accountId = 1L;

        Account account = Mockito.mock(Account.class);
        when(account.getContacts()).thenReturn(new EnumMap<>(AccountContactType.class));

        Map<AccountContactType, String> updatedContactTypes =
            new EnumMap<>(AccountContactType.class);
        updatedContactTypes.put(AccountContactType.PRIMARY, "primaryNew");
        updatedContactTypes.put(AccountContactType.FINANCIAL, "financialNew");
        updatedContactTypes.put(AccountContactType.SERVICE, "primaryNew");

        Map<String, AuthorityStatus> operatorStatuses =
            Map.of(
                "primaryNew", AuthorityStatus.ACTIVE,
                "financialNew", AuthorityStatus.ACTIVE
            );

        List<String> finalUsers = List.of("primaryNew", "financialNew");
        final ServiceContactDetails serviceContactDetails = ServiceContactDetails.builder()
                .roleCode("rolecode")
                .name("name")
                .email("email")
                .build();

        when(authorityService.existsByUserIdAndAccountId("primaryNew", accountId)).thenReturn(true);
        when(authorityService.existsByUserIdAndAccountId("financialNew", accountId)).thenReturn(true);
        when(accountRepository.findById(accountId)).thenReturn(
            Optional.of(account));
        when(authorityService.findStatusByUsersAndAccountId(finalUsers, accountId)).thenReturn(operatorStatuses);
        when(accountContactQueryService.getServiceContactDetails(accountId)).thenReturn(Optional.of(serviceContactDetails));

        //invoke
        service.updateAccountContacts(updatedContactTypes, accountId);

        Map<AccountContactType, String> expectedContactTypes =
            new EnumMap<>(AccountContactType.class);
        expectedContactTypes.put(AccountContactType.PRIMARY, "primaryNew");
        expectedContactTypes.put(AccountContactType.FINANCIAL, "financialNew");
        expectedContactTypes.put(AccountContactType.SERVICE, "primaryNew");

        verify(authorityService, times(2)).existsByUserIdAndAccountId("primaryNew", accountId);
        verify(authorityService, times(1)).existsByUserIdAndAccountId("financialNew", accountId);
        verifyNoMoreInteractions(authorityService);
        verify(financialContactValidator, times(1)).validateUpdate(expectedContactTypes, accountId);
        verify(primaryContactValidator, times(1)).validateUpdate(expectedContactTypes, accountId);
        verify(eventPublisher, times(1)).publishEvent(
            FirstPrimaryContactAssignedToAccountEvent.builder().accountId(accountId).userId("primaryNew").build());
        verify(eventPublisher, times(1)).publishEvent(
                FirstServiceContactAssignedToAccountEvent.builder().accountId(accountId).serviceContactDetails(serviceContactDetails).build());
    }
    
    @Test
    void updateAccountContacts_updated_user_not_related_to_account() {
        Long accountId = 1L;
        
        Map<AccountContactType, String> updatedContactTypes = 
                new EnumMap<>(AccountContactType.class);
        updatedContactTypes.put(AccountContactType.PRIMARY, "primaryNew");
        updatedContactTypes.put(AccountContactType.FINANCIAL, "financialDisabled");
        
        when(authorityService.existsByUserIdAndAccountId("primaryNew", accountId)).thenReturn(true);
        when(authorityService.existsByUserIdAndAccountId("financialDisabled", accountId)).thenReturn(false);
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.updateAccountContacts(updatedContactTypes, accountId);
        });
        
        //assert
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_ACCOUNT);
        verify(authorityService, times(1)).existsByUserIdAndAccountId("primaryNew", accountId);
        verify(authorityService, times(1)).existsByUserIdAndAccountId("financialDisabled", accountId);
        verifyNoMoreInteractions(authorityService);
        verifyNoInteractions(accountRepository, financialContactValidator, primaryContactValidator, eventPublisher);
        
    }
}

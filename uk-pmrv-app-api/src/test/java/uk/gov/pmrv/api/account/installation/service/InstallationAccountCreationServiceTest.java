package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.service.AccountIdentifierService;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationAccountCreationServiceTest {

    @InjectMocks
    private InstallationAccountCreationService installationAccountCreationService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountIdentifierService accountIdentifierService;

    @Mock
    private LegalEntityService legalEntityService;

    @Mock
    private InstallationAccountQueryService installationAccountQueryService;

    @Mock
    private InstallationAccountMapper installationAccountMapper;

    @Test
    void createAccount() {
        final AppUser appUser = AppUser.builder().build();
        long identifierId = 2L;
        
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        InstallationAccount account = InstallationAccount.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).build())
                .build();
        
        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .build();

        InstallationAccount accountSaved = InstallationAccount.builder()
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).name("le").build())
                .build();
        InstallationAccountDTO accountDTOSaved = InstallationAccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build())
                .build();

        when(accountIdentifierService.incrementAndGet()).thenReturn(identifierId);
        when(legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), appUser)).thenReturn(legalEntity);
        when(installationAccountMapper.toInstallationAccount(accountDTO, identifierId)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(accountSaved);
        when(installationAccountMapper.toInstallationAccountDTO(accountSaved)).thenReturn(accountDTOSaved);
        
        //invoke
        InstallationAccountDTO result = installationAccountCreationService.createAccount(accountDTO, appUser);

        assertThat(result).isEqualTo(accountDTOSaved);

        verify(accountIdentifierService, times(1)).incrementAndGet();
        verify(legalEntityService, times(1)).resolveLegalEntity(accountDTO.getLegalEntity(), appUser);
        verify(installationAccountMapper, times(1)).toInstallationAccount(accountDTO, identifierId);

        ArgumentCaptor<InstallationAccount> accountCaptor = ArgumentCaptor.forClass(InstallationAccount.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        InstallationAccount accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(InstallationAccountStatus.UNAPPROVED);
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(legalEntity);
    }

    @Test
    void createAccount_for_migration_with_id() {
        final AppUser appUser = AppUser.builder().build();
        long identifierId = 2L;

        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();
        InstallationAccount account = InstallationAccount.builder()
                .id(identifierId)
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).build())
                .build();

        LegalEntity legalEntity = LegalEntity.builder()
                .id(1L).name("le")
                .build();

        InstallationAccount accountSaved = InstallationAccount.builder()
                .name("account")
                .legalEntity(LegalEntity.builder().id(1L).name("le").build())
                .build();
        InstallationAccountDTO accountDTOSaved = InstallationAccountDTO.builder()
                .name("account")
                .legalEntity(LegalEntityDTO.builder().id(1L).name("le").build())
                .build();

        when(legalEntityService.resolveLegalEntity(accountDTO.getLegalEntity(), appUser)).thenReturn(legalEntity);
        when(installationAccountMapper.toInstallationAccount(accountDTO, identifierId)).thenReturn(account);
        when(accountRepository.save(account)).thenReturn(accountSaved);
        when(installationAccountMapper.toInstallationAccountDTO(accountSaved)).thenReturn(accountDTOSaved);

        //invoke
        InstallationAccountDTO result = installationAccountCreationService.createAccount(accountDTO, appUser);

        assertThat(result).isEqualTo(accountDTOSaved);

        verify(accountIdentifierService, never()).incrementAndGet();
        verify(legalEntityService, times(1)).resolveLegalEntity(accountDTO.getLegalEntity(), appUser);
        verify(installationAccountMapper, times(1)).toInstallationAccount(accountDTO, identifierId);

        ArgumentCaptor<InstallationAccount> accountCaptor = ArgumentCaptor.forClass(InstallationAccount.class);
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        InstallationAccount accountCaptured = accountCaptor.getValue();
        assertThat(accountCaptured.getStatus()).isEqualTo(InstallationAccountStatus.UNAPPROVED);
        assertThat(accountCaptured.getLegalEntity()).isEqualTo(legalEntity);
    }

    @Test
    void createAccount_account_not_valid_should_throw_exception() {
        final String accountName = "account";
        final AppUser appUser = AppUser.builder().build();
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder()
                .name(accountName)
                .legalEntity(LegalEntityDTO.builder().id(1L).build())
                .build();

        doThrow(new BusinessException((MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS))).when(installationAccountQueryService)
            .validateAccountNameExistence(accountDTO.getName());

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> installationAccountCreationService.createAccount(accountDTO, appUser));

        assertThat(businessException.getErrorCode()).isEqualTo(MetsErrorCode.ACCOUNT_REGISTRATION_NUMBER_ALREADY_EXISTS);

        verify(installationAccountQueryService, times(1)).validateAccountNameExistence(accountName);
        verifyNoInteractions(legalEntityService, accountIdentifierService);
        verifyNoMoreInteractions(accountRepository);
    }
}

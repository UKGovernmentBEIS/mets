package uk.gov.pmrv.api.account.installation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountSearchResultsInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountIdAndNameAndLegalEntityNameDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountWithoutLeHoldingCompanyDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.installation.domain.enumeration.TransferCodeStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.account.service.VerifierAccountAccessByAccountTypeService;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

@ExtendWith(MockitoExtension.class)
class InstallationAccountQueryServiceTest {

    @InjectMocks
    private InstallationAccountQueryService service;

    @Mock
    private InstallationAccountRepository installationAccountRepository;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private InstallationAccountMapper accountInstallationMapper;

    @Mock
    private VerifierAccountAccessByAccountTypeService verifierAccountAccessService;

    @Test
    void getAccountDTOById() {
        Long id = 1L;
        InstallationAccount account = InstallationAccount.builder().id(id).name("name").build();
        InstallationAccountDTO accountDTO = InstallationAccountDTO.builder().id(id).name("name").build();

        when(installationAccountRepository.findAccountFullInfoById(id)).thenReturn(Optional.of(account));
        when(accountInstallationMapper.toInstallationAccountDTO(account)).thenReturn(accountDTO);

        InstallationAccountDTO result = service.getAccountDTOById(id);

        assertEquals(accountDTO, result);

        verify(installationAccountRepository, times(1)).findAccountFullInfoById(id);
        verify(accountInstallationMapper, times(1)).toInstallationAccountDTO(account);
    }

    @Test
    void getAccountWithoutLeHoldingCompanyDTOById() {
        Long id = 1L;
        String name = "account";
        InstallationAccountStatus status = InstallationAccountStatus.SURRENDERED;
        EmitterType emitterType = EmitterType.HSE;
        InstallationCategory installationCategory = InstallationCategory.N_A;
        InstallationAccount account = InstallationAccount.builder()
            .id(id)
            .name(name)
            .siteName(name)
            .status(status)
            .accountType(AccountType.INSTALLATION)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .build();
        InstallationAccountWithoutLeHoldingCompanyDTO accountDetailsDTO = InstallationAccountWithoutLeHoldingCompanyDTO.builder()
            .name(name)
            .siteName(name)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .build();

        when(installationAccountRepository.findAccountWithLocAndLeWithLocById(id)).thenReturn(Optional.of(account));
        when(accountInstallationMapper.toInstallationAccountWithoutLeHoldingCompany(account)).thenReturn(accountDetailsDTO);

        InstallationAccountWithoutLeHoldingCompanyDTO result = service.getAccountWithoutLeHoldingCompanyDTOById(id);

        assertEquals(accountDetailsDTO, result);
        verify(installationAccountRepository, times(1)).findAccountWithLocAndLeWithLocById(id);
        verify(accountInstallationMapper, times(1)).toInstallationAccountWithoutLeHoldingCompany(account);
    }

    @Test
    void getAccountWithoutLeHoldingCompanyDTOById_account_not_found() {
        Long id = 1L;

        when(installationAccountRepository.findAccountWithLocAndLeWithLocById(id)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.getAccountWithoutLeHoldingCompanyDTOById(id));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());

        verify(installationAccountRepository, times(1)).findAccountWithLocAndLeWithLocById(id);
        verifyNoInteractions(accountInstallationMapper);
    }

    @Test
    void getAccountIdsByStatus() {
        InstallationAccountStatus status = InstallationAccountStatus.LIVE;
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        InstallationAccount account1 = InstallationAccount.builder().id(accountId1).status(status).build();
        InstallationAccount account2 = InstallationAccount.builder().id(accountId2).status(status).build();

        when(installationAccountRepository.findAllByStatusIs(status)).thenReturn(List.of(account1, account2));

        List<Long> result = service.getAccountIdsByStatus(status);

        // Verify
        assertThat(result).containsExactlyInAnyOrder(accountId1, accountId2);
        verify(installationAccountRepository, times(1)).findAllByStatusIs(status);
    }
    
    @Test
    void getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes() {
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	Set<InstallationAccountStatus> statuses = Set.of(InstallationAccountStatus.LIVE);
    	Set<InstallationCategory> categories = Set.of(InstallationCategory.A);
    	Set<EmitterType> emitterTypes = Set.of(EmitterType.GHGE);
    	
    	InstallationAccountIdAndNameAndLegalEntityNameDTO mockedDTO = mock(InstallationAccountIdAndNameAndLegalEntityNameDTO.class);
    	
		when(installationAccountRepository.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories))
				.thenReturn(Set.of(mockedDTO));
		
		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> result = service.getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories);
		
		assertThat(result).containsExactlyInAnyOrder(mockedDTO);
		verify(installationAccountRepository, times(1)).findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, emitterTypes, categories);
    }
    
    @Test
    void getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes_null_inputs() {
    	CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
    	Set<InstallationAccountStatus> statuses = null;
    	Set<InstallationCategory> categories = null;
    	
    	InstallationAccountIdAndNameAndLegalEntityNameDTO mockedDTO = mock(InstallationAccountIdAndNameAndLegalEntityNameDTO.class);
    	
		when(installationAccountRepository.findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, Set.of(), Set.of(), Set.of()))
				.thenReturn(Set.of(mockedDTO));
		
		Set<InstallationAccountIdAndNameAndLegalEntityNameDTO> result = service.getAccountIdsByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, statuses, Set.of(), categories);
		
		assertThat(result).containsExactlyInAnyOrder(mockedDTO);
		verify(installationAccountRepository, times(1)).findAllByCAAndStatusesAndInstallationCategoriesAndEmitterTypes(ca, Set.of(), Set.of(), Set.of());
    }

    @Test
    void existsAccountById() {
        Long accountId = 1L;

        when(installationAccountRepository.existsById(accountId)).thenReturn(true);

        assertTrue(service.existsAccountById(accountId));

        verify(installationAccountRepository, times(1)).existsById(accountId);
    }

    @Test
    void getAccountsByUserAndSearchCriteria_operator() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        String accountName1 = "account1";
        String accountName2 = "account2";
        PmrvUser operatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();
        operatorUser.setAuthorities(List.of(
            PmrvAuthority.builder().accountId(accountId1).code("operator").build(),
            PmrvAuthority.builder().accountId(accountId2).code("operator").build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AccountSearchResultsInfoDTO> accountDTOs =
            List.of(
                new AccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", InstallationAccountStatus.LIVE.name(), "lename1"),
                new AccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", InstallationAccountStatus.LIVE.name(), "lename2"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(installationAccountRepository.findByAccountIds(List.of(accountId1, accountId2), criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = service.getAccountsByUserAndSearchCriteria(operatorUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountSearchResultsInfoDTO::getName)
            .containsOnly(accountName1, accountName2);

        //verify
        verify(installationAccountRepository, times(1)).findByAccountIds(List.of(accountId1, accountId2), criteria);
        verifyNoMoreInteractions(installationAccountRepository);
    }

    @Test
    void getAccountsByUserAndSearchCriteria_regulator() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        String accountName1 = "account1";
        String accountName2 = "account2";
        CompetentAuthorityEnum ca = CompetentAuthorityEnum.ENGLAND;
        PmrvUser regulatorUser = PmrvUser.builder().userId("userId").roleType(RoleType.REGULATOR).build();
        regulatorUser.setAuthorities(List.of(
            PmrvAuthority.builder().competentAuthority(ca).permissions(List.of(Permission.PERM_CA_USERS_EDIT)).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AccountSearchResultsInfoDTO> accountDTOs =
            List.of(
                new AccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", InstallationAccountStatus.LIVE.name(), "lename1"),
                new AccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", InstallationAccountStatus.LIVE.name(), "lename2"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(installationAccountRepository.findByCompAuth(CompetentAuthorityEnum.ENGLAND, criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = service.getAccountsByUserAndSearchCriteria(regulatorUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountSearchResultsInfoDTO::getName)
            .containsOnly(accountName1, accountName2);

        //verify
        verify(installationAccountRepository, times(1)).findByCompAuth(CompetentAuthorityEnum.ENGLAND, criteria);
        verifyNoMoreInteractions(installationAccountRepository);
    }

    @Test
    void getAccountsByUserAndSearchCriteria_verifierAdmin() {
        Long vbId = 1L;
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Set<Long> accountIdsSet = Set.of(accountId1, accountId2);
        List<Long> accountIds = new ArrayList<>(accountIdsSet);
        String accountName1 = "account1";
        String accountName2 = "account2";
        PmrvUser verifierUser = PmrvUser.builder().userId("userId").roleType(RoleType.VERIFIER).build();
        verifierUser.setAuthorities(List.of(
                PmrvAuthority.builder().verificationBodyId(vbId).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AccountSearchResultsInfoDTO> accountDTOs =
                List.of(
                        new AccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", InstallationAccountStatus.LIVE.name(), "lename1"),
                        new AccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", InstallationAccountStatus.LIVE.name(), "lename2"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser, AccountType.INSTALLATION)).thenReturn(accountIdsSet);
        when(installationAccountRepository.findByAccountIds(accountIds, criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = service.getAccountsByUserAndSearchCriteria(verifierUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountSearchResultsInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(verifierAccountAccessService, times(1)).findAuthorizedAccountIds(verifierUser, AccountType.INSTALLATION);
        verify(installationAccountRepository, times(1)).findByAccountIds(accountIds, criteria);
        verifyNoMoreInteractions(installationAccountRepository);
    }

    @Test
    void getAccountsByUserAndSearchCriteria_verifier() {
        Long accountId1 = 1L;
        Long accountId2 = 2L;
        Set<Long> accountIdsSet = Set.of(accountId1, accountId2);
        List<Long> accountIds = new ArrayList<>(accountIdsSet);
        String accountName1 = "account1";
        String accountName2 = "account2";
        Long vbId = 1L;
        PmrvUser verifierUser = PmrvUser.builder().userId("userId").roleType(RoleType.VERIFIER).build();
        verifierUser.setAuthorities(List.of(
            PmrvAuthority.builder()
                .verificationBodyId(vbId).permissions(List.of(Permission.PERM_CA_USERS_EDIT)).build()
        ));

        AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        List<AccountSearchResultsInfoDTO> accountDTOs =
            List.of(
                new AccountSearchResultsInfoDTO(accountId1, accountName1, "EM00001", InstallationAccountStatus.LIVE.name(), "legalEntityName"),
                new AccountSearchResultsInfoDTO(accountId2, accountName2, "EM00002", InstallationAccountStatus.LIVE.name(), "legalEntityName"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(verifierAccountAccessService.findAuthorizedAccountIds(verifierUser, AccountType.INSTALLATION)).thenReturn(accountIdsSet);
        when(installationAccountRepository.findByAccountIds(accountIds, criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = service.getAccountsByUserAndSearchCriteria(verifierUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountSearchResultsInfoDTO::getName)
            .containsOnly(accountName1, accountName2);

        //verify
        verify(verifierAccountAccessService, times(1)).findAuthorizedAccountIds(verifierUser, AccountType.INSTALLATION);
        verify(installationAccountRepository, times(1)).findByAccountIds(accountIds, criteria);
        verifyNoMoreInteractions(installationAccountRepository);
    }

    @Test
    void isLegalEntityUnused() {
        Long legalEntityId = 1L;

        when(accountQueryService.isLegalEntityUnused(legalEntityId)).thenReturn(true);

        assertTrue(service.isLegalEntityUnused(legalEntityId));

        verify(accountQueryService, times(1)).isLegalEntityUnused(legalEntityId);
    }

    @Test
    void transferCodeExists() {
        String code = "code";

        when(installationAccountRepository.existsByTransferCode(code)).thenReturn(false);

        assertFalse(service.transferCodeExists(code));

        verify(installationAccountRepository, times(1)).existsByTransferCode(code);
    }

    @Test
    void getByActiveTransferCode() {
        final String transferCode = "code";
        final InstallationAccount installationAccount = InstallationAccount.builder().id(1L).build();
        final InstallationAccountDTO installationAccountDTO = InstallationAccountDTO.builder().id(1L).build();

        when(installationAccountRepository.findAccountByTransferCodeAndTransferCodeStatus(transferCode, TransferCodeStatus.ACTIVE))
                .thenReturn(Optional.of(installationAccount));
        when(accountInstallationMapper.toInstallationAccountDTO(installationAccount))
                .thenReturn(installationAccountDTO);

        // Invoke
        Optional<InstallationAccountDTO> actual = service.getByActiveTransferCode(transferCode);

        // Verify
        assertThat(actual).isPresent().contains(installationAccountDTO);
        verify(installationAccountRepository, times(1))
                .findAccountByTransferCodeAndTransferCodeStatus(transferCode, TransferCodeStatus.ACTIVE);
        verify(accountInstallationMapper, times(1))
                .toInstallationAccountDTO(installationAccount);
    }

    @Test
    void validateAccountNameExistence() {
        String name = "name";

        when(accountQueryService.isExistingActiveAccountName(name)).thenReturn(false);
        service.validateAccountNameExistence(name);

        verify(accountQueryService, times(1)).isExistingActiveAccountName(name);
    }

    @Test
    void validateAccountNameExistence_name_exists() {
        String name = "name";

        when(accountQueryService.isExistingActiveAccountName(name)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class, () -> service.validateAccountNameExistence(name));
        assertEquals(ErrorCode.ACCOUNT_ALREADY_EXISTS, be.getErrorCode());

        verify(accountQueryService, times(1)).isExistingActiveAccountName(name);
    }

    @Test
    void getInstallationAccountInfoDTOById() {
        Long accountId = 1L;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.A;
        String transferCode = "code";
        InstallationAccount account = InstallationAccount.builder()
            .id(accountId)
            .name("account")
            .status(InstallationAccountStatus.SURRENDERED)
            .accountType(AccountType.INSTALLATION)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .transferCode(transferCode)
            .build();
        InstallationAccountInfoDTO accountInfoDTO = InstallationAccountInfoDTO.builder()
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .transferCode(transferCode)
            .build();

        when(installationAccountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountInstallationMapper.toInstallationAccountInfoDTO(account)).thenReturn(accountInfoDTO);

        InstallationAccountInfoDTO result = service.getInstallationAccountInfoDTOById(accountId);

        assertEquals(accountInfoDTO, result);
        verify(installationAccountRepository, times(1)).findById(accountId);
        verify(accountInstallationMapper, times(1)).toInstallationAccountInfoDTO(account);
    }

    @Test
    void getInstallationAccountInfoDTOById_account_not_found() {
        Long accountId = 1L;

        when(installationAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () -> service.getInstallationAccountInfoDTOById(accountId));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());

        verify(installationAccountRepository, times(1)).findById(accountId);
        verifyNoInteractions(accountInstallationMapper);
    }

    @Test
    void getAccountInstallationCategoryById() {
        Long accountId = 1L;
        final InstallationAccount account = InstallationAccount.builder()
                .id(accountId)
                .installationCategory(InstallationCategory.A)
                .build();

        when(installationAccountRepository.findById(accountId)).thenReturn(Optional.of(account));

        // Invoke
        InstallationCategory expected = service.getAccountInstallationCategoryById(accountId);

        // Verify
        assertEquals(InstallationCategory.A, expected);
        verify(installationAccountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountInstallationCategoryById_not_found() {
        Long accountId = 1L;

        when(installationAccountRepository.findById(accountId)).thenReturn(Optional.empty());

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.getAccountInstallationCategoryById(accountId));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
        verify(installationAccountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountWithLeById() {
        Long accountId = 1L;
        final InstallationAccount account = InstallationAccount.builder().id(accountId).build();

        when(installationAccountRepository.findAccountWithLeById(accountId))
                .thenReturn(Optional.of(account));

        // Invoke
        InstallationAccount expected = service.getAccountWithLeById(accountId);

        // Verify
        assertEquals(account, expected);
        verify(installationAccountRepository, times(1)).findAccountWithLeById(accountId);
    }

    @Test
    void getAccountWithLeById_not_found() {
        Long accountId = 1L;

        when(installationAccountRepository.findAccountWithLeById(accountId))
                .thenReturn(Optional.empty());

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.getAccountWithLeById(accountId));

        // Verify
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, be.getErrorCode());
        verify(installationAccountRepository, times(1)).findAccountWithLeById(accountId);
    }
}
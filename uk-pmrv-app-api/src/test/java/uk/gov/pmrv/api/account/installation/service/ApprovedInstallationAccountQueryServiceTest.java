package uk.gov.pmrv.api.account.installation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovedInstallationAccountQueryServiceTest {

    @InjectMocks
    private ApprovedInstallationAccountQueryService approvedInstallationAccountService;

    @Mock
    private InstallationAccountRepository installationAccountRepository;

    @Mock
    private InstallationAccountMapper installationAccountMapper;

    @Test
    void getApprovedAccountById() {
        Long accountId = 1L;
        String accountName = "accountName";

        InstallationAccount account = InstallationAccount.builder()
            .id(accountId)
            .name(accountName)
            .status(InstallationAccountStatus.LIVE)
            .build();

        InstallationAccountInfoDTO accountInfo = InstallationAccountInfoDTO.builder()
            .id(accountId)
            .name(accountName)
            .status(InstallationAccountStatus.LIVE)
            .build();

        when(installationAccountRepository
            .findByIdAndStatusNotIn(accountId, List.of(InstallationAccountStatus.UNAPPROVED, InstallationAccountStatus.DENIED)))
            .thenReturn(Optional.of(account));
        when(installationAccountMapper.toInstallationAccountInfoDTO(account)).thenReturn(accountInfo);

        Optional<InstallationAccountInfoDTO> optionalResult = approvedInstallationAccountService.getApprovedAccountById(accountId);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(accountInfo, optionalResult.get());
    }

    @Test
    void getApprovedAccountById_returns_empty() {
        Long accountId = 1L;

        when(installationAccountRepository
            .findByIdAndStatusNotIn(accountId, List.of(InstallationAccountStatus.UNAPPROVED, InstallationAccountStatus.DENIED)))
            .thenReturn(Optional.empty());

        Optional<InstallationAccountInfoDTO> optionalResult = approvedInstallationAccountService.getApprovedAccountById(accountId);

        assertThat(optionalResult).isEmpty();

        verifyNoInteractions(installationAccountMapper);
    }

    @Test
    void isAccountApproved() {
        InstallationAccount account = InstallationAccount.builder().status(InstallationAccountStatus.DENIED).build();
        assertFalse(approvedInstallationAccountService.isAccountApproved(account));

        account.setStatus(InstallationAccountStatus.TRANSFERRED);
        assertTrue(approvedInstallationAccountService.isAccountApproved(account));

        account.setStatus(InstallationAccountStatus.UNAPPROVED);
        assertFalse(approvedInstallationAccountService.isAccountApproved(account));
    }
    
    @Test
    void getAllApprovedAccountIdsByCa() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        List<Long> expectedAccountIds = List.of(1L, 2L);

        when(installationAccountRepository
            .findAccountIdsByCaAndStatusNotIn(competentAuthority, List.of(InstallationAccountStatus.UNAPPROVED, InstallationAccountStatus.DENIED)))
            .thenReturn(expectedAccountIds);

        List<Long> accountIds = approvedInstallationAccountService.getAllApprovedAccountIdsByCa(competentAuthority);

        assertThat(accountIds).containsExactlyInAnyOrderElementsOf(expectedAccountIds);
    }

    @Test
    void getApprovedAccountsAndCaSiteContactsByCa() {
        int page = 1;
        int pageSize = 20;
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        AccountContactType caSiteContactType = AccountContactType.CA_SITE;
        List<AccountContactInfoDTO> expectedContacts = List.of(AccountContactInfoDTO.builder()
            .accountId(1L).accountName("name").userId("userId").build());
        Page<AccountContactInfoDTO> pagedAccountContacts = new PageImpl<>(expectedContacts);

        when(installationAccountRepository
            .findAccountContactsByCaAndContactTypeAndStatusNotIn(PageRequest.of(page, pageSize), competentAuthority,
                caSiteContactType, List.of(InstallationAccountStatus.UNAPPROVED, InstallationAccountStatus.DENIED)))
            .thenReturn(pagedAccountContacts);

        Page<AccountContactInfoDTO> resultPage =
            approvedInstallationAccountService.getApprovedAccountsAndCaSiteContactsByCa(competentAuthority, page, pageSize);

        assertThat(resultPage).containsExactlyInAnyOrderElementsOf(expectedContacts);

    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.INSTALLATION, approvedInstallationAccountService.getAccountType());
    }
}
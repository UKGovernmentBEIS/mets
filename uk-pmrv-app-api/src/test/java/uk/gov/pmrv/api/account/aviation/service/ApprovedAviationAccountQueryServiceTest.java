package uk.gov.pmrv.api.account.aviation.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApprovedAviationAccountQueryServiceTest {

    @InjectMocks
    private ApprovedAviationAccountQueryService approvedAviationAccountService;

    @Mock
    private AviationAccountRepository aviationAccountRepository;

    @Mock
    private AviationAccountMapper aviationAccountMapper;

    @Test
    void isAccountApproved() {
        AviationAccount account = AviationAccount.builder().status(AviationAccountStatus.LIVE).build();
        assertTrue(approvedAviationAccountService.isAccountApproved(account));

        account.setStatus(AviationAccountStatus.CLOSED);
        assertFalse(approvedAviationAccountService.isAccountApproved(account));

        account.setStatus(AviationAccountStatus.NEW);
        assertTrue(approvedAviationAccountService.isAccountApproved(account));
    }

    @Test
    void getAllApprovedAccountIdsByCa() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        List<Long> expectedAccountIds = List.of(1L, 2L);

        when(aviationAccountRepository
            .findAccountIdsByCaAndStatusNotIn(competentAuthority, List.of(AviationAccountStatus.CLOSED)))
            .thenReturn(expectedAccountIds);

        List<Long> accountIds = approvedAviationAccountService.getAllApprovedAccountIdsByCa(competentAuthority);

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

        when(aviationAccountRepository
            .findAccountContactsByCaAndContactTypeAndStatusNotIn(PageRequest.of(page, pageSize), competentAuthority,
                caSiteContactType, List.of(AviationAccountStatus.CLOSED)))
            .thenReturn(pagedAccountContacts);

        Page<AccountContactInfoDTO> resultPage =
            approvedAviationAccountService.getApprovedAccountsAndCaSiteContactsByCa(competentAuthority, page, pageSize);

        assertThat(resultPage).containsExactlyInAnyOrderElementsOf(expectedContacts);

    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, approvedAviationAccountService.getAccountType());
    }

    @Test
    void getApprovedAccountById() {
        Long accountId = 1L;
        String accountName = "accountName";
        String crcoCode = "crcoCode";

        AviationAccount account = AviationAccount.builder()
                .id(accountId)
                .name(accountName)
                .crcoCode(crcoCode)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.NORTHERN_IRELAND)
                .status(AviationAccountStatus.NEW)
                .build();

        AviationAccountInfoDTO accountInfo = AviationAccountInfoDTO.builder()
                .id(accountId)
                .name(accountName)
                .crcoCode(crcoCode)
                .accountType(AccountType.AVIATION)
                .competentAuthority(CompetentAuthorityEnum.NORTHERN_IRELAND)
                .build();

        when(aviationAccountRepository
            .findByIdAndStatusNotIn(accountId, List.of(AviationAccountStatus.CLOSED)))
            .thenReturn(Optional.of(account));
        when(aviationAccountMapper.toAviationAccountInfoDTO(account)).thenReturn(accountInfo);

        Optional<AviationAccountInfoDTO> optionalResult = approvedAviationAccountService.getApprovedAccountById(accountId);

        assertThat(optionalResult).isNotEmpty();
        assertEquals(accountInfo, optionalResult.get());

        verify(aviationAccountRepository, times(1))
                .findByIdAndStatusNotIn(accountId, List.of(AviationAccountStatus.CLOSED));
        verify(aviationAccountMapper, times(1)).toAviationAccountInfoDTO(account);
    }

    @Test
    void getApprovedAccountById_returns_empty() {
        Long accountId = 1L;

        when(aviationAccountRepository
            .findByIdAndStatusNotIn(accountId, List.of(AviationAccountStatus.CLOSED)))
            .thenReturn(Optional.empty());

        assertThat(approvedAviationAccountService.getApprovedAccountById(accountId)).isEmpty();

        verify(aviationAccountRepository, times(1))
                .findByIdAndStatusNotIn(accountId, List.of(AviationAccountStatus.CLOSED));
        verify(aviationAccountMapper, never()).toAviationAccountInfoDTO(any());
    }
}

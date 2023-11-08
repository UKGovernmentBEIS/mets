package uk.gov.pmrv.api.account.aviation.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.aviation.domain.AviationAccount;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.repository.AviationAccountRepository;
import uk.gov.pmrv.api.account.aviation.transform.AviationAccountMapper;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.ApprovedAccountTypeQueryService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprovedAviationAccountQueryService implements ApprovedAccountTypeQueryService {

    private final AviationAccountRepository aviationAccountRepository;
    private final AviationAccountMapper aviationAccountMapper;

    public Optional<AviationAccountInfoDTO> getApprovedAccountById(Long accountId) {
        return aviationAccountRepository.findByIdAndStatusNotIn(accountId, getStatusesConsideredNotApproved())
            .map(aviationAccountMapper::toAviationAccountInfoDTO);
    }

    @Override
    public List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority) {
        return aviationAccountRepository.findAccountIdsByCaAndStatusNotIn(competentAuthority, getStatusesConsideredNotApproved());
    }

    @Override
    public Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority,
                                                                                Integer page, Integer pageSize) {
        return aviationAccountRepository.findAccountContactsByCaAndContactTypeAndStatusNotIn(
            PageRequest.of(page, pageSize),
            competentAuthority,
            AccountContactType.CA_SITE,
            getStatusesConsideredNotApproved()
        );
    }

    @Override
    public boolean isAccountApproved(Account account) {
        return ((AviationAccount) account).getStatus() != AviationAccountStatus.CLOSED;
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.AVIATION;
    }

    private List<AviationAccountStatus> getStatusesConsideredNotApproved() {
        return List.of(AviationAccountStatus.CLOSED);
    }
}

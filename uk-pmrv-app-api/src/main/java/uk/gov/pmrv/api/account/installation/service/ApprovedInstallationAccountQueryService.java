package uk.gov.pmrv.api.account.installation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccount;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountInfoDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.repository.InstallationAccountRepository;
import uk.gov.pmrv.api.account.installation.transform.InstallationAccountMapper;
import uk.gov.pmrv.api.account.service.ApprovedAccountTypeQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprovedInstallationAccountQueryService implements ApprovedAccountTypeQueryService {

    private final InstallationAccountRepository installationAccountRepository;
    private final InstallationAccountMapper installationAccountMapper;

    public Optional<InstallationAccountInfoDTO> getApprovedAccountById(Long accountId) {
        return installationAccountRepository.findByIdAndStatusNotIn(accountId, getStatusesConsideredNotApproved())
            .map(installationAccountMapper::toInstallationAccountInfoDTO);
    }

    @Override
    public List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority) {
        return installationAccountRepository.findAccountIdsByCaAndStatusNotIn(competentAuthority, getStatusesConsideredNotApproved());
    }

    @Override
    public Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority,
                                                                                Integer page, Integer pageSize) {
        return installationAccountRepository.findAccountContactsByCaAndContactTypeAndStatusNotIn(
            PageRequest.of(page, pageSize),
            competentAuthority,
            AccountContactType.CA_SITE,
            getStatusesConsideredNotApproved()
        );
    }

    @Override
    public boolean isAccountApproved(Account account) {
        InstallationAccountStatus accountStatus = ((InstallationAccount) account).getStatus();
        return !getStatusesConsideredNotApproved().contains(accountStatus);
    }

    @Override
    public AccountType getAccountType() {
        return AccountType.INSTALLATION;
    }

    private List<InstallationAccountStatus> getStatusesConsideredNotApproved() {
        return List.of(InstallationAccountStatus.UNAPPROVED, InstallationAccountStatus.DENIED);
    }
}

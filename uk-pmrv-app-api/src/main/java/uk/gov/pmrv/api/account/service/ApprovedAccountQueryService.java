package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApprovedAccountQueryService {

    private final List<ApprovedAccountTypeQueryService> approvedAccountTypeQueryServices;

    public List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority, AccountType accountType) {
        return getAccountTypeService(accountType)
            .map(service -> service.getAllApprovedAccountIdsByCa(competentAuthority))
            .orElse(Collections.emptyList());
    }

    public Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority,
                                                                                AccountType accountType,
                                                                                Integer page, Integer pageSize) {
        return getAccountTypeService(accountType)
            .map(service -> service.getApprovedAccountsAndCaSiteContactsByCa(competentAuthority, page, pageSize))
            .orElse(Page.empty());
    }

    public boolean isAccountApproved(Account account) {
        return getAccountTypeService(account.getAccountType())
            .map(service -> service.isAccountApproved(account))
            .orElse(false);
    }

    private Optional<ApprovedAccountTypeQueryService> getAccountTypeService(AccountType accountType) {
        return approvedAccountTypeQueryServices.stream()
            .filter(service -> service.getAccountType().equals(accountType))
            .findAny();
    }
}

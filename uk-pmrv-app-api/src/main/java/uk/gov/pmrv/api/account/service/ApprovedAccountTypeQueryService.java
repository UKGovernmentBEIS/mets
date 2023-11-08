package uk.gov.pmrv.api.account.service;

import org.springframework.data.domain.Page;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;

public interface ApprovedAccountTypeQueryService {

    List<Long> getAllApprovedAccountIdsByCa(CompetentAuthorityEnum competentAuthority);

    Page<AccountContactInfoDTO> getApprovedAccountsAndCaSiteContactsByCa(CompetentAuthorityEnum competentAuthority, Integer page, Integer pageSize);

    boolean isAccountApproved(Account account);

    AccountType getAccountType();
}

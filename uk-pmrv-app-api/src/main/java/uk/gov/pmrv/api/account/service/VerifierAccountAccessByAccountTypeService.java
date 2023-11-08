package uk.gov.pmrv.api.account.service;

import java.util.Set;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

public interface VerifierAccountAccessByAccountTypeService {

    Set<Long> findAuthorizedAccountIds(PmrvUser user, AccountType accountType);
}

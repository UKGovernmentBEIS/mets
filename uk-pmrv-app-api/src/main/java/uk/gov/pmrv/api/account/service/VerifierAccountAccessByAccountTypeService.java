package uk.gov.pmrv.api.account.service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.Set;

public interface VerifierAccountAccessByAccountTypeService {

    Set<Long> findAuthorizedAccountIds(AppUser user, AccountType accountType);
}

package uk.gov.pmrv.api.account.repository;

import uk.gov.pmrv.api.account.domain.Account;

import java.util.Optional;

public interface AccountCustomRepository {
    Optional<Account> findByIdForUpdate(Long id);
}

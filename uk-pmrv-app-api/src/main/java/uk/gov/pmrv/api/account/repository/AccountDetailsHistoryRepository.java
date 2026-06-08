package uk.gov.pmrv.api.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.AccountDetailsHistory;

public interface AccountDetailsHistoryRepository extends JpaRepository<AccountDetailsHistory, Long> {

    @Transactional(readOnly = true)
    Page<AccountDetailsHistory> findByAccountIdOrderByCreationDateDesc(Pageable pageable, Long accountId);

}

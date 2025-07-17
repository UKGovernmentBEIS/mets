package uk.gov.pmrv.api.account.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.AccountNote;

import java.util.Optional;

public interface AccountNoteRepository extends JpaRepository<AccountNote, Long> {

    @Transactional(readOnly = true)
    Page<AccountNote> findAccountNotesByAccountIdOrderByLastUpdatedOnDesc(Pageable pageable, Long accountId);

    @Transactional(readOnly = true)
    @Query("select accountNote.accountId from AccountNote accountNote where accountNote.id = :id")
    Optional<Long> getAccountIdById(Long id);
}

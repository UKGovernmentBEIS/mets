package uk.gov.pmrv.api.account.aviation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusHistory;

@Repository
public interface AviationAccountReportingStatusHistoryRepository extends JpaRepository<AviationAccountReportingStatusHistory, Long> {

    @Transactional(readOnly = true)
    Page<AviationAccountReportingStatusHistory> findByAccountIdOrderBySubmissionDateDesc(Pageable pageable, Long accountId);

}

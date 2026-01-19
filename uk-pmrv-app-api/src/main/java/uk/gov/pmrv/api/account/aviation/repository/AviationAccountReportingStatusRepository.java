package uk.gov.pmrv.api.account.aviation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;

import java.time.Year;
import java.util.List;
import java.util.Optional;

public interface AviationAccountReportingStatusRepository extends JpaRepository<AviationAccountReportingStatus, Long> {

    @Transactional(readOnly = true)
    Optional<AviationAccountReportingStatus> findByAccountIdAndYear(Long accountId, Year year); // TODO: is anyone using this?

    @Transactional(readOnly = true)
    Page<AviationAccountReportingStatus> findByAccountIdOrderByYearDesc(Pageable pageable, Long accountId);

    @Transactional(readOnly = true)
    List<AviationAccountReportingStatus> findByAccountIdOrderByYearDesc(Long accountId);

    @Transactional(readOnly = true)
    boolean existsByAccountIdAndYear(Long accountId, Year year);

}

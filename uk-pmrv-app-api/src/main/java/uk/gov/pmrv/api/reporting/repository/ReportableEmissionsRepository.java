package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ReportableEmissionsRepository extends JpaRepository<ReportableEmissionsEntity, Long> {

    Optional<ReportableEmissionsEntity> findByAccountIdAndYear(Long accountId, Year year);

    @Transactional(readOnly = true)
    List<ReportableEmissionsEntity> findAllByAccountIdAndYearIn(Long accountId, Set<Year> years);
}

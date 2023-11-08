package uk.gov.pmrv.api.aviationreporting.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;

import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface AviationReportableEmissionsRepository extends JpaRepository<AviationReportableEmissionsEntity, Long> {

    @Transactional(readOnly = true)
    List<AviationReportableEmissionsEntity> findAllByAccountIdAndYearIn(Long accountId, Set<Year> years);

    @Transactional(readOnly = true)
    Optional<AviationReportableEmissionsEntity> findByAccountIdAndYear(Long accountId, Year year);
}

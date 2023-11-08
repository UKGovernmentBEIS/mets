package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.RegionalInventoryData;

import java.time.Year;
import java.util.Optional;

@Repository
public interface RegionalInventoryDataRepository extends JpaRepository<RegionalInventoryData, Long> {

    @Transactional(readOnly = true)
    Optional<RegionalInventoryData> findByReportingYearAndChargingZoneCode(Year reportingYear, String chargingZoneCode);

    @Transactional(readOnly = true)
    boolean existsByReportingYear(Year reportingYear);
}

package uk.gov.pmrv.api.reporting.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.NationalInventoryData;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Repository
public interface NationalInventoryDataRepository extends JpaRepository<NationalInventoryData, Long> {

    @Transactional(readOnly = true)
    @EntityGraph(value = "sector-graph", type = EntityGraph.EntityGraphType.FETCH)
    List<NationalInventoryData> findByReportingYearOrderBySectorAscFuelAsc(Year reportingYear);

    @Transactional(readOnly = true)
    @Query(name = NationalInventoryData.NAMED_QUERY_FIND_BY_REPORTING_YEAR_AND_SECTOR_AND_FUEL)
    Optional<NationalInventoryData> findByReportingYearAndSectorAndFuel(Year year, String sector, String fuel);

    @Transactional(readOnly = true)
    boolean existsByReportingYear(Year reportingYear);
}

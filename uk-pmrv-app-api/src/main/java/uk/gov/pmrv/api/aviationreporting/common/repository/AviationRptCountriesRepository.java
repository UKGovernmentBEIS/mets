package uk.gov.pmrv.api.aviationreporting.common.repository;

import java.time.Year;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptCountriesEntity;

@Repository
public interface AviationRptCountriesRepository extends JpaRepository<AviationRptCountriesEntity, Long> {
    
    @Transactional(readOnly = true)
    @Query(
        "select new org.springframework.data.util.Pair(airport.icao, country.state) from AviationRptCountriesEntity country " +
            "join AviationRptAirportsEntity airport on country.country = airport.country " +
            "where airport.icao in (:icaos) and country.year = :currentYear " +
            "group by airport.icao, country.state"
    )
    List<Pair<String, String>> findCountryStatesByAirportIcaoInAndYear(Set<String> icaos, Year currentYear);

}

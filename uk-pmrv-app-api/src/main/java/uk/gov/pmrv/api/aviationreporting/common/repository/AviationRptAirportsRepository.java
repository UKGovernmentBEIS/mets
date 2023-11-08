package uk.gov.pmrv.api.aviationreporting.common.repository;

import java.time.Year;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptAirportsEntity;

import java.util.List;
import java.util.Set;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;

@Repository
public interface AviationRptAirportsRepository extends JpaRepository<AviationRptAirportsEntity, Long> {

    @Transactional(readOnly = true)
    @Query("select distinct new uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO " +
        "(airport.icao, airport.name, airport.country, airport.countryType, country.state) " +
        " from AviationRptCountriesEntity country join AviationRptAirportsEntity airport on country.country = airport.country " +
        " where airport.icao in (:icaos) and country.year = :year")
    List<AviationRptAirportsDTO> findAllByIcaoInAndYear(Set<String> icaos, Year year);

    @Transactional(readOnly = true)
    @Query("select airport.icao " +
            " from AviationRptCountriesEntity country join AviationRptAirportsEntity airport on country.country = airport.country " +
            " where airport.icao in (:icaos) and country.isChapter3 ")
    List<String> findChapter3Icaos(Set<String> icaos);

}

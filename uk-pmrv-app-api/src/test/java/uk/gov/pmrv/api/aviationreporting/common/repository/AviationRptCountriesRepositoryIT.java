package uk.gov.pmrv.api.aviationreporting.common.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.util.Pair;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptAirportsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptCountriesEntity;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;

import java.time.Year;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationRptCountriesRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationRptCountriesRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findCountryStatesByAirportIcaoInAndYear() {

        createAirport("EGLL", "LONDON HEATHROW", "United Kingdom", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED);
        createAirport("LEPA", "PALMA DE MALLORCA", "Spain", CountryType.EEA_COUNTRY);
        createAirport("LGAT", "ATHINAI/HELLINIKON", "Greece", CountryType.EEA_COUNTRY);

        createCountry("United Kingdom", "United Kingdom", Year.of(2022), Boolean.TRUE);
        createCountry("Greece", "Greece", Year.of(2022), Boolean.FALSE);

        flushAndClear();

        List<Pair<String, String>> actual = repository.findCountryStatesByAirportIcaoInAndYear(Set.of("EGLL", "LEPA", "LGAT"), Year.of(2022));

        assertThat(actual).hasSize(2);
        assertThat(actual).extracting(Pair::getFirst).containsOnly("EGLL", "LGAT");
        assertThat(actual).extracting(Pair::getSecond).containsOnly("United Kingdom", "Greece");
    }

    private AviationRptAirportsEntity createAirport(String icao, String name, String country, CountryType countryType) {
        AviationRptAirportsEntity airport = AviationRptAirportsEntity.builder()
                .icao(icao)
                .name(name)
                .country(country)
                .countryType(countryType)
                .build();

        entityManager.persist(airport);
        return airport;
    }

    private AviationRptCountriesEntity createCountry(String countryName, String state, Year year, Boolean isChapter3) {
        AviationRptCountriesEntity country = AviationRptCountriesEntity.builder()
                .country(countryName)
                .year(year)
                .state(state)
                .isChapter3(isChapter3)
                .build();

        entityManager.persist(country);
        return country;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}

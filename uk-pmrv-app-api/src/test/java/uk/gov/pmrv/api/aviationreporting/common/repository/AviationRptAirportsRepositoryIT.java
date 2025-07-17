package uk.gov.pmrv.api.aviationreporting.common.repository;

import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Year;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.netz.api.common.AbstractContainerBaseTest;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptAirportsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationRptCountriesEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AviationRptAirportsRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AviationRptAirportsRepository repository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void findAllByIcaoInAndYear() {

        createAirport("EGLL", "LONDON HEATHROW", "United Kingdom", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED);
        createAirport("LGAT", "ATHINAI/HELLINIKON", "Greece", CountryType.EEA_COUNTRY);
        createCountry("United Kingdom", "United Kingdom", Year.of(2022), Boolean.TRUE);
        createCountry("Greece", "Greece", Year.of(2022), Boolean.FALSE);
        
        flushAndClear();

        List<AviationRptAirportsDTO> foundAirports = repository.findAllByIcaoInAndYear(Set.of("EGLL", "LGAT"), Year.of(2022));

        assertThat(foundAirports)
            .hasSize(2)
            .containsExactlyInAnyOrderElementsOf(List.of(
            AviationRptAirportsDTO.builder().icao("EGLL").name("LONDON HEATHROW").state("United Kingdom")
                .country("United Kingdom").countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED).build(),
            AviationRptAirportsDTO.builder().icao("LGAT").name("ATHINAI/HELLINIKON").state("Greece")
                .country("Greece").countryType(CountryType.EEA_COUNTRY).build()
        ));
    }
    
    @Test
    void findChapter3Icaos() {

        createAirport("EGLL", "LONDON HEATHROW", "United Kingdom", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED);
        createAirport("LEPA", "PALMA DE MALLORCA", "Spain", CountryType.EEA_COUNTRY);
        createAirport("LGAT", "ATHINAI/HELLINIKON", "Greece", CountryType.EEA_COUNTRY);
        createAirport("LOXA", "AIGEN", "Austria", CountryType.EEA_COUNTRY);
        createAirport("EBCV", "CHIEVRES", "Belgium", CountryType.EEA_COUNTRY);

        createCountry("United Kingdom", "United Kingdom", Year.of(2022), Boolean.TRUE);
        createCountry("Greece", "Greece", Year.of(2022), Boolean.FALSE);
        createCountry("Austria", "Austria", Year.of(2022), Boolean.TRUE);
        createCountry("Belgium", "Belgium", Year.of(2022), Boolean.FALSE);

        flushAndClear();

        List<String> actual = repository.findChapter3Icaos(Set.of("EGLL", "LEPA", "LGAT", "LOXA", "EBCV"), Year.of(2022));

        assertThat(actual).hasSize(2);
        assertThat(actual).containsOnly("EGLL", "LOXA");
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
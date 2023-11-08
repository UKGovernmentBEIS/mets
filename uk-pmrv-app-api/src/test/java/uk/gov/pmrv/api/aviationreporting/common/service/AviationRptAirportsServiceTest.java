package uk.gov.pmrv.api.aviationreporting.common.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.Year;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationRptAirportsRepository;

@ExtendWith(MockitoExtension.class)
class AviationRptAirportsServiceTest {

    @InjectMocks
    private AviationRptAirportsService aviationRptAirportsService;

    @Mock
    private AviationRptAirportsRepository aviationRptAirportsRepository;

    @Test
    void getAirportsByIcaoCodes() {
        String icao1 = "LEPA";
        String icao2 = "EGLL";

        AviationRptAirportsDTO dto1 = AviationRptAirportsDTO.builder()
            .icao(icao1)
            .name("PALMA DE MALLORCA")
            .country("Spain")
            .countryType(CountryType.EEA_COUNTRY)
            .build();
        AviationRptAirportsDTO dto2 = AviationRptAirportsDTO.builder()
            .icao(icao2)
            .name("LONDON HEATHROW")
            .country("United Kingdom")
            .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
            .build();

        when(aviationRptAirportsRepository.findAllByIcaoInAndYear(Set.of(icao1, icao2), Year.of(2022)))
                .thenReturn(List.of(dto1, dto2));

        List<AviationRptAirportsDTO> result = aviationRptAirportsService.getAirportsByIcaoCodesAndYear(Set.of(icao1, icao2), Year.of(2022));

        assertThat(result).hasSize(2);

        assertThat(result.get(0).getIcao()).isEqualTo(icao1);
        assertThat(result.get(0).getName()).isEqualTo("PALMA DE MALLORCA");
        assertThat(result.get(0).getCountry()).isEqualTo("Spain");
        assertThat(result.get(0).getCountryType()).isEqualTo(CountryType.EEA_COUNTRY);

        assertThat(result.get(1).getIcao()).isEqualTo(icao2);
        assertThat(result.get(1).getName()).isEqualTo("LONDON HEATHROW");
        assertThat(result.get(1).getCountry()).isEqualTo("United Kingdom");
        assertThat(result.get(1).getCountryType()).isEqualTo(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED);
    }

    @Test
    void findChapter3Icaos() {

        String icao1 = "LEPA";
        String icao2 = "EGLL";

        when(aviationRptAirportsRepository.findChapter3Icaos(Set.of(icao1, icao2)))
                .thenReturn(List.of(icao1, icao2));

        final List<String> actual = aviationRptAirportsService.findChapter3Icaos(Set.of(icao1, icao2));
        assertThat(actual).hasSize(2);
        assertThat(actual).containsOnly(icao1, icao2);
    }
}

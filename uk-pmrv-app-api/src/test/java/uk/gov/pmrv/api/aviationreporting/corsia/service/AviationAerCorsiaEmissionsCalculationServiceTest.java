package uk.gov.pmrv.api.aviationreporting.corsia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.dto.AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaim;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsreductionclaim.AviationAerCorsiaEmissionsReductionClaimDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaAerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaInternationalFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaStandardFuelsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaSubmittedEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaEmissionsCalculationServiceTest {

    @InjectMocks
    private AviationAerCorsiaSubmittedEmissionsCalculationService aviationAerCorsiaEmissionsCalculationService;

    @Mock
    private AviationRptAirportsService airportsService;

    @Test
    void calculateSubmittedEmissions() {
        AviationAerCorsiaEmissionsCalculationDTO emissionsCalculationDTO = createEmissionsCalculationDTO();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingYear(Year.of(2023))
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(emissionsCalculationDTO.getAggregatedEmissionsData())
                        .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                                .exist(true)
                                .emissionsReductionClaimDetails(AviationAerCorsiaEmissionsReductionClaimDetails.builder()
                                        .totalEmissions(emissionsCalculationDTO.getEmissionsReductionClaim())
                                        .build())
                                .build())
                        .build())
                .build();

        AviationAerCorsiaTotalEmissions expectedTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsNumber(60)
                .allFlightsEmissions(BigDecimal.valueOf(18660))
                .offsetFlightsNumber(40)
                .offsetFlightsEmissions(BigDecimal.valueOf(12460))
                .nonOffsetFlightsNumber(20)
                .nonOffsetFlightsEmissions(BigDecimal.valueOf(6200))
                .emissionsReductionClaim(BigDecimal.valueOf(123.45))
                .build();

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of("ICAO1", "ICAO2", "ICAO5", "ICAO6"));

        // Invoke
        AviationAerCorsiaSubmittedEmissions result = aviationAerCorsiaEmissionsCalculationService
                .calculateSubmittedEmissions(aerContainer);

        // Verify
        assertThat(result.getTotalEmissions()).isEqualTo(expectedTotalEmissions);
        assertThat(result.getAerodromePairsTotalEmissions()).hasSize(3);
        assertThat(result.getStandardFuelsTotalEmissions()).hasSize(2);
        assertThat(result.getFlightsEmissions().getFlightsEmissionsDetails()).hasSize(2);
        verify(airportsService, times(3))
                .findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6"));
    }

    @Test
    void calculateSubmittedEmissions_no_emissions_reduction() {
        AviationAerCorsiaEmissionsCalculationDTO emissionsCalculationDTO = createEmissionsCalculationDTO();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingYear(Year.of(2023))
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(emissionsCalculationDTO.getAggregatedEmissionsData())
                        .emissionsReductionClaim(AviationAerCorsiaEmissionsReductionClaim.builder()
                                .exist(false)
                                .build())
                        .build())
                .build();

        AviationAerCorsiaTotalEmissions expectedTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
                .allFlightsNumber(60)
                .allFlightsEmissions(BigDecimal.valueOf(18660))
                .offsetFlightsNumber(40)
                .offsetFlightsEmissions(BigDecimal.valueOf(12460))
                .nonOffsetFlightsNumber(20)
                .nonOffsetFlightsEmissions(BigDecimal.valueOf(6200))
                .emissionsReductionClaim(BigDecimal.ZERO)
                .build();

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of("ICAO1", "ICAO2", "ICAO5", "ICAO6"));

        // Invoke
        AviationAerCorsiaSubmittedEmissions result = aviationAerCorsiaEmissionsCalculationService
                .calculateSubmittedEmissions(aerContainer);

        // Verify
        assertThat(result.getTotalEmissions()).isEqualTo(expectedTotalEmissions);
        assertThat(result.getAerodromePairsTotalEmissions()).hasSize(3);
        assertThat(result.getStandardFuelsTotalEmissions()).hasSize(2);
        assertThat(result.getFlightsEmissions().getFlightsEmissionsDetails()).hasSize(2);
        verify(airportsService, times(3))
                .findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6"));
    }

    @Test
    void calculateTotalEmissions() {

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of("ICAO1", "ICAO2", "ICAO5", "ICAO6"));

        final AviationAerCorsiaTotalEmissions actual = aviationAerCorsiaEmissionsCalculationService.calculateTotalEmissions(createEmissionsCalculationDTO());
        assertThat(actual.getAllFlightsNumber()).isEqualTo(60);
        assertThat(actual.getAllFlightsEmissions()).isEqualByComparingTo(new BigDecimal("18660"));
        assertThat(actual.getOffsetFlightsNumber()).isEqualTo(40);
        assertThat(actual.getOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("12460"));
        assertThat(actual.getNonOffsetFlightsNumber()).isEqualTo(20);
        assertThat(actual.getNonOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("6200"));
        assertThat(actual.getEmissionsReductionClaim()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void calculateTotalEmissions_no_offsetting_flights() {

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of());

        final AviationAerCorsiaTotalEmissions actual = aviationAerCorsiaEmissionsCalculationService.calculateTotalEmissions(createEmissionsCalculationDTO());
        assertThat(actual.getAllFlightsNumber()).isEqualTo(60);
        assertThat(actual.getAllFlightsEmissions()).isEqualByComparingTo(new BigDecimal("18660"));
        assertThat(actual.getOffsetFlightsNumber()).isZero();
        assertThat(actual.getOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("0"));
        assertThat(actual.getNonOffsetFlightsNumber()).isEqualTo(60);
        assertThat(actual.getNonOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("18660"));
        assertThat(actual.getEmissionsReductionClaim()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void calculateTotalEmissions_no_non_offsetting_flights() {

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6"));

        final AviationAerCorsiaTotalEmissions actual = aviationAerCorsiaEmissionsCalculationService.calculateTotalEmissions(createEmissionsCalculationDTO());
        assertThat(actual.getAllFlightsNumber()).isEqualTo(60);
        assertThat(actual.getAllFlightsEmissions()).isEqualByComparingTo(new BigDecimal("18660"));
        assertThat(actual.getOffsetFlightsNumber()).isEqualTo(60);
        assertThat(actual.getOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("18660"));
        assertThat(actual.getNonOffsetFlightsNumber()).isZero();
        assertThat(actual.getNonOffsetFlightsEmissions()).isEqualByComparingTo(new BigDecimal("0"));
        assertThat(actual.getEmissionsReductionClaim()).isEqualByComparingTo(new BigDecimal("123.45"));
    }

    @Test
    void getEmissionTradingScheme() {
        final EmissionTradingScheme actual = aviationAerCorsiaEmissionsCalculationService.getEmissionTradingScheme();
        assertEquals(EmissionTradingScheme.CORSIA, actual);
    }

    @Test
    void calculateAerodromePairsTotalEmissions() {

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6")))
                .thenReturn(List.of("ICAO1", "ICAO2", "ICAO4", "ICAO5", "ICAO6"));

        final List<AviationAerCorsiaAerodromePairsTotalEmissions> actual = aviationAerCorsiaEmissionsCalculationService.calculateAerodromePairsTotalEmissions(createEmissionsCalculationDTO());
        assertThat(actual).hasSize(3);
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO1", "ICAO3", "ICAO5");
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport1", "Airport3", "Airport5");
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO2", "ICAO4", "ICAO6");
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport2", "Airport4", "Airport6");
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getFlightsNumber).containsOnly(10, 20, 30);
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getEmissions).containsOnly(new BigDecimal("3160.000"), new BigDecimal("6200.000"), new BigDecimal("9300.000"));
        assertThat(actual).extracting(AviationAerCorsiaAerodromePairsTotalEmissions::getOffset).containsOnly(Boolean.TRUE, Boolean.FALSE, Boolean.TRUE);

    }

    @Test
    void testCalculateStandardFuelsEmissions_all_fuel_types() {

        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = AviationAerCorsiaEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country3")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.TS_1)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.NO_3_JET_FUEL)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(20)
                                        .build()
                        ))
                        .build())
                .build();

        List<AviationAerCorsiaStandardFuelsTotalEmissions> result = aviationAerCorsiaEmissionsCalculationService
                .calculateStandardFuelsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);

        assertThat(result).hasSize(5);
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getFuelConsumption).containsOnly(new BigDecimal("4500.391"), new BigDecimal("1500.234"), new BigDecimal("2000.345"), new BigDecimal("2500.567"), new BigDecimal("3000.145"));
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getEmissions).containsOnly(new BigDecimal("14221.236"), new BigDecimal("4650.725"), new BigDecimal("6201.070"), new BigDecimal("7901.792"), new BigDecimal("9480.458"));
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getFuelType).containsOnly(AviationAerCorsiaFuelType.JET_KEROSENE, AviationAerCorsiaFuelType.JET_GASOLINE, AviationAerCorsiaFuelType.AVIATION_GASOLINE, AviationAerCorsiaFuelType.NO_3_JET_FUEL, AviationAerCorsiaFuelType.TS_1);
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getEmissionsFactor).containsOnly(AviationAerCorsiaFuelType.JET_KEROSENE.getEmissionFactor(), AviationAerCorsiaFuelType.JET_GASOLINE.getEmissionFactor(), AviationAerCorsiaFuelType.AVIATION_GASOLINE.getEmissionFactor(), AviationAerCorsiaFuelType.NO_3_JET_FUEL.getEmissionFactor(), AviationAerCorsiaFuelType.TS_1.getEmissionFactor());

    }

    @Test
    void testCalculateStandardFuelsEmissions_not_all_fuel_types() {

        AviationAerCorsiaEmissionsCalculationDTO aviationAerCorsiaEmissionsCalculationDTO = AviationAerCorsiaEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country3")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerCorsiaFuelType.NO_3_JET_FUEL)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(20)
                                        .build()
                        ))
                        .build())
                .build();

        List<AviationAerCorsiaStandardFuelsTotalEmissions> result = aviationAerCorsiaEmissionsCalculationService
                .calculateStandardFuelsTotalEmissions(aviationAerCorsiaEmissionsCalculationDTO);

        assertThat(result).hasSize(4);
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getFuelConsumption).containsOnly(new BigDecimal("1000.123"), new BigDecimal("1500.234"), new BigDecimal("2500.567"), new BigDecimal("3000.145"));
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getEmissions).containsOnly(new BigDecimal("3160.389"), new BigDecimal("4650.725"), new BigDecimal("7751.758"), new BigDecimal("9480.458"));
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getFuelType).containsOnly(AviationAerCorsiaFuelType.JET_KEROSENE, AviationAerCorsiaFuelType.JET_GASOLINE, AviationAerCorsiaFuelType.AVIATION_GASOLINE, AviationAerCorsiaFuelType.NO_3_JET_FUEL);
        assertThat(result).extracting(AviationAerCorsiaStandardFuelsTotalEmissions::getEmissionsFactor).containsOnly(AviationAerCorsiaFuelType.JET_KEROSENE.getEmissionFactor(), AviationAerCorsiaFuelType.JET_GASOLINE.getEmissionFactor(), AviationAerCorsiaFuelType.AVIATION_GASOLINE.getEmissionFactor(), AviationAerCorsiaFuelType.NO_3_JET_FUEL.getEmissionFactor());
    }

    @Test
    void calculateAerCorsiaFlightsEmissions_all_fuel_types() {

        AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerCorsiaInternationalFlightsEmissionsCalculationDTO.builder()
            .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport1")
                            .icao("ICAO1")
                            .country("Country1")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE1")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport2")
                            .icao("ICAO2")
                            .country("Country2")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE2")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                        .fuelConsumption(new BigDecimal("1000.123"))
                        .flightsNumber(10)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport3")
                            .icao("ICAO3")
                            .country("Country3")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE1")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport4")
                            .icao("ICAO4")
                            .country("Country4")
                            .countryType(CountryType.CLOSELY_CONNECTED_TO_EEA_COUNTRY)
                            .state("STATE2")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                        .fuelConsumption(new BigDecimal("1500.234"))
                        .flightsNumber(20)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport5")
                            .icao("ICAO5")
                            .country("Country5")
                            .countryType(CountryType.CLOSELY_CONNECTED_TO_EEA_COUNTRY)
                            .state("STATE3")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport6")
                            .icao("ICAO6")
                            .country("Country6")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE4")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(new BigDecimal("2000.345"))
                        .flightsNumber(30)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport5")
                            .icao("ICAO5")
                            .country("Country5")
                            .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                            .state("STATE3")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport6")
                            .icao("ICAO6")
                            .country("Country6")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE4")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                        .fuelConsumption(new BigDecimal("2500.567"))
                        .flightsNumber(40)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport7")
                            .icao("ICAO7")
                            .country("Country7")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE5")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport8")
                            .icao("ICAO8")
                            .country("Country8")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE6")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.NO_3_JET_FUEL)
                        .fuelConsumption(new BigDecimal("3000.145"))
                        .flightsNumber(50)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport9")
                            .icao("ICAO9")
                            .country("Country9")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE5")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport10")
                            .icao("ICAO10")
                            .country("Country10")
                            .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                            .state("STATE6")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.NO_3_JET_FUEL)
                        .fuelConsumption(new BigDecimal("3500.268"))
                        .flightsNumber(60)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport11")
                            .icao("ICAO11")
                            .country("Country11")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE7")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport10")
                            .icao("ICAO10")
                            .country("Country10")
                            .countryType(CountryType.THIRD_COUNTRY)
                            .state("STATE6")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.TS_1)
                        .fuelConsumption(new BigDecimal("3700.375"))
                        .flightsNumber(70)
                        .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                            .name("Airport9")
                            .icao("ICAO9")
                            .country("Country9")
                            .countryType(CountryType.EEA_COUNTRY)
                            .state("STATE5")
                            .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                            .name("Airport12")
                            .icao("ICAO12")
                            .country("Country12")
                            .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                            .state("STATE8")
                            .build())
                        .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                        .fuelConsumption(new BigDecimal("3800.743"))
                        .flightsNumber(80)
                        .build()
                ))
                    .build())
                .year(Year.of(2023))
                .build();

        when(airportsService.findChapter3Icaos(Set.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO5", "ICAO6", "ICAO7", "ICAO8", "ICAO9", "ICAO10", "ICAO11", "ICAO12")))
            .thenReturn(List.of("ICAO1", "ICAO2", "ICAO3", "ICAO4", "ICAO8", "ICAO9", "ICAO10", "ICAO11"));

        AviationAerCorsiaInternationalFlightsEmissions result = aviationAerCorsiaEmissionsCalculationService
            .calculateInternationalFlightsEmissions(aviationAerEmissionsCalculationDTO);
        final List<AviationAerCorsiaInternationalFlightsEmissionsDetails> flightsEmissionsDetails = result.getFlightsEmissionsDetails();
        assertThat(flightsEmissionsDetails).hasSize(6);
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerCorsiaFlightsEmissionsDetails::getFuelConsumption).containsOnly(new BigDecimal("2500.357"), new BigDecimal("2000.345"), new BigDecimal("2500.567"), new BigDecimal("6500.413"), new BigDecimal("3700.375"), new BigDecimal("3800.743"));
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerCorsiaFlightsEmissionsDetails::getEmissions).containsOnly(new BigDecimal("7901.128"), new BigDecimal("6201.070"), new BigDecimal("7901.792"), new BigDecimal("20541.305"), new BigDecimal("11693.185"), new BigDecimal("11782.303"));
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerCorsiaFlightsEmissionsDetails::getFuelType).containsOnly(AviationAerCorsiaFuelType.JET_KEROSENE, AviationAerCorsiaFuelType.JET_GASOLINE, AviationAerCorsiaFuelType.AVIATION_GASOLINE, AviationAerCorsiaFuelType.TS_1, AviationAerCorsiaFuelType.NO_3_JET_FUEL);
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerCorsiaFlightsEmissionsDetails::isOffset).containsOnly(true, false, false, true, true, false);
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerCorsiaFlightsEmissionsDetails::getFlightsNumber).containsOnly( 30, 30, 40, 110, 70, 80);
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getDepartureState).containsOnly("STATE1", "STATE3", "STATE5", "STATE5", "STATE7", "STATE5");
        assertThat(flightsEmissionsDetails).extracting(AviationAerCorsiaInternationalFlightsEmissionsDetails::getArrivalState).containsOnly("STATE2", "STATE4", "STATE6", "STATE6", "STATE6", "STATE8");
    }

    private AviationAerCorsiaEmissionsCalculationDTO createEmissionsCalculationDTO() {
        Set<AviationAerCorsiaAggregatedEmissionDataDetails> aggregatedEmissionDataDetails = new HashSet<>();
        aggregatedEmissionDataDetails.add(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                .airportFrom(AviationRptAirportsDTO.builder()
                        .name("Airport1")
                        .icao("ICAO1")
                        .country("Country1")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .airportTo(AviationRptAirportsDTO.builder()
                        .name("Airport2")
                        .icao("ICAO2")
                        .country("Country2")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                .fuelConsumption(new BigDecimal(1000))
                .flightsNumber(10)
                .build()
        );
        aggregatedEmissionDataDetails.add(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                .airportFrom(AviationRptAirportsDTO.builder()
                        .name("Airport3")
                        .icao("ICAO3")
                        .country("Country3")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .airportTo(AviationRptAirportsDTO.builder()
                        .name("Airport4")
                        .icao("ICAO4")
                        .country("Country4")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                .fuelConsumption(new BigDecimal(2000))
                .flightsNumber(20)
                .build()
        );
        aggregatedEmissionDataDetails.add(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                .airportFrom(AviationRptAirportsDTO.builder()
                        .name("Airport5")
                        .icao("ICAO5")
                        .country("Country5")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .airportTo(AviationRptAirportsDTO.builder()
                        .name("Airport6")
                        .icao("ICAO6")
                        .country("Country6")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                .fuelConsumption(new BigDecimal(3000))
                .flightsNumber(30)
                .build()
        );

        AviationAerCorsiaAggregatedEmissionsData aggregatedEmissionsData = AviationAerCorsiaAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(aggregatedEmissionDataDetails)
                .build();

        return AviationAerCorsiaEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(aggregatedEmissionsData)
                .emissionsReductionClaim(BigDecimal.valueOf(123.45))
                .build();
    }
}

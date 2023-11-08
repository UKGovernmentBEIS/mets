package uk.gov.pmrv.api.aviationreporting.ukets.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.dto.AviationAerEmissionsCalculationDTO;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSafDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AerodromePairsTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerNonDomesticFlightsEmissionsDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.AviationAerTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions.StandardFuelsTotalEmissions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsEmissionsCalculationServiceTest {

    @InjectMocks
    private AviationAerUkEtsSubmittedEmissionsCalculationService aviationAerUkEtsEmissionsCalculationService;

    @Test
    void testCalculateTotalEmissions() {

        AviationAerTotalEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateTotalEmissions(this.createEmissionCalculationDto());

        assertThat(result.getNumFlightsCoveredByUkEts()).isEqualTo(30);
        assertThat(result.getStandardFuelEmissions()).isEqualByComparingTo(new BigDecimal("9350.00"));
        assertThat(result.getReductionClaimEmissions()).isEqualByComparingTo(new BigDecimal("4725"));
        assertThat(result.getTotalEmissions()).isEqualByComparingTo(new BigDecimal("4625"));
    }
    
    @Test
    void testCalculateTotalEmissions_negativeTotalEmissionsConvertedToZero() {

    	AviationAerEmissionsCalculationDTO dto = this.createEmissionCalculationDto();
    	AviationAerSafDetails details = AviationAerSafDetails.builder()
                .totalSafMass(new BigDecimal(1500))
                .emissionsFactor(new BigDecimal("3.15"))
                .totalEmissionsReductionClaim(new BigDecimal(10000))
                .build();
    	AviationAerSaf saf = AviationAerSaf.builder()
                .exist(true)
                .safDetails(details)
                .build();
    	dto.setSaf(saf);
        AviationAerTotalEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateTotalEmissions(dto);

        assertThat(result.getStandardFuelEmissions()).isEqualByComparingTo(new BigDecimal("9350.00"));
        assertThat(result.getReductionClaimEmissions()).isEqualByComparingTo(new BigDecimal("10000"));
        assertThat(result.getTotalEmissions()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testCalculateStandardFuelsEmissions_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(20)
                                        .build()
                        ))
                        .build())
                .build();

        List<StandardFuelsTotalEmissions> result = aviationAerUkEtsEmissionsCalculationService
                .calculateStandardFuelsTotalEmissions(aviationAerEmissionsCalculationDTO);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(StandardFuelsTotalEmissions::getFuelConsumption).containsOnly(new BigDecimal("3500.690"), new BigDecimal("4500.379"), new BigDecimal("5500.613"));
        assertThat(result).extracting(StandardFuelsTotalEmissions::getEmissions).containsOnly(new BigDecimal("11027.174"), new BigDecimal("13951.175"), new BigDecimal("17051.900"));
        assertThat(result).extracting(StandardFuelsTotalEmissions::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.JET_GASOLINE, AviationAerUkEtsFuelType.AVIATION_GASOLINE);
        assertThat(result).extracting(StandardFuelsTotalEmissions::getEmissionsFactor).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE.getEmissionFactor(), AviationAerUkEtsFuelType.JET_GASOLINE.getEmissionFactor(), AviationAerUkEtsFuelType.AVIATION_GASOLINE.getEmissionFactor());
        assertThat(result).extracting(StandardFuelsTotalEmissions::getNetCalorificValue).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE.getNetCalorificValue(), AviationAerUkEtsFuelType.JET_GASOLINE.getNetCalorificValue(), AviationAerUkEtsFuelType.AVIATION_GASOLINE.getNetCalorificValue());

    }

    @Test
    void testCalculateStandardFuelsEmissions_not_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(20)
                                        .build()
                        ))
                        .build())
                .build();

        List<StandardFuelsTotalEmissions> result = aviationAerUkEtsEmissionsCalculationService
                .calculateStandardFuelsTotalEmissions(aviationAerEmissionsCalculationDTO);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(StandardFuelsTotalEmissions::getFuelConsumption).containsOnly(new BigDecimal("3500.690"), new BigDecimal("4500.379"));
        assertThat(result).extracting(StandardFuelsTotalEmissions::getEmissions).containsOnly(new BigDecimal("11027.174"), new BigDecimal("13951.175"));
        assertThat(result).extracting(StandardFuelsTotalEmissions::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.JET_GASOLINE);
        assertThat(result).extracting(StandardFuelsTotalEmissions::getEmissionsFactor).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE.getEmissionFactor(), AviationAerUkEtsFuelType.JET_GASOLINE.getEmissionFactor());
        assertThat(result).extracting(StandardFuelsTotalEmissions::getNetCalorificValue).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE.getNetCalorificValue(), AviationAerUkEtsFuelType.JET_GASOLINE.getNetCalorificValue());
    }

    @Test
    void calculateAerodromePairsTotalEmissions_all_different_pairs() {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(50)
                                        .build()
                        ))
                        .build())
                .build();

        final List<AerodromePairsTotalEmissions> actual =
                aviationAerUkEtsEmissionsCalculationService.calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO);

        assertThat(actual).hasSize(5);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO1", "ICAO3", "ICAO5", "ICAO7", "ICAO9");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport1", "Airport3", "Airport5", "Airport7", "Airport9");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO2", "ICAO4", "ICAO6", "ICAO8", "ICAO10");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport2", "Airport4", "Airport6", "Airport8", "Airport10");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getFlightsNumber).containsOnly(10, 20, 30, 40, 50);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getEmissions).containsOnly(new BigDecimal("3150.387"), new BigDecimal("4650.725"), new BigDecimal("6201.070"), new BigDecimal("9300.450"), new BigDecimal("10850.831"));
    }

    @Test
    void calculateAerodromePairsTotalEmissions_with_same_pairs() {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2500.345"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(50)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("3200.145"))
                                        .flightsNumber(70)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(80)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("3700.567"))
                                        .flightsNumber(90)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("4000.897"))
                                        .flightsNumber(100)
                                        .build()
                        ))
                        .build())
                .build();

        final List<AerodromePairsTotalEmissions> actual =
                aviationAerUkEtsEmissionsCalculationService.calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO);

        assertThat(actual).hasSize(5);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO1", "ICAO3", "ICAO5", "ICAO7", "ICAO9");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport1", "Airport3", "Airport5", "Airport7", "Airport9");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO2", "ICAO4", "ICAO6", "ICAO8", "ICAO10");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport2", "Airport4", "Airport6", "Airport8", "Airport10");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getFlightsNumber).containsOnly(10, 20, 70, 120, 270);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getEmissions).containsOnly(new BigDecimal("3150.387"), new BigDecimal("4650.725"), new BigDecimal("14077.156"), new BigDecimal("19380.906"), new BigDecimal("34910.398"));
    }

    @Test
    void calculateAerodromePairsTotalEmissions_with_many_arrival_airports() {
        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build()
                        ))
                        .build())
                .build();

        final List<AerodromePairsTotalEmissions> actual =
                aviationAerUkEtsEmissionsCalculationService.calculateAerodromePairsTotalEmissions(aviationAerEmissionsCalculationDTO);

        assertThat(actual).hasSize(3);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO1", "ICAO1", "ICAO1");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getDepartureAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport1", "Airport1", "Airport1");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getIcao).containsOnly("ICAO2", "ICAO4", "ICAO6");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getArrivalAirport).extracting(AviationRptAirportsDTO::getName).containsOnly("Airport2", "Airport4", "Airport6");
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getFlightsNumber).containsOnly(10, 20, 30);
        assertThat(actual).extracting(AerodromePairsTotalEmissions::getEmissions).containsOnly(new BigDecimal("3150.387"), new BigDecimal("4650.725"), new BigDecimal("6201.070"));
    }

    @Test
    void calculateDomesticFlightsEmissions_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country3")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(50)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(60)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport11")
                                                .icao("ICAO11")
                                                .country("Country11")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3700.375"))
                                        .flightsNumber(70)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport12")
                                                .icao("ICAO12")
                                                .country("Country12")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3800.743"))
                                        .flightsNumber(80)
                                        .build()
                        ))
                        .build())
                .build();

        AviationAerDomesticFlightsEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
        final List<AviationAerDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = result.getDomesticFlightsEmissionsDetails();
        assertThat(domesticFlightsEmissionsDetails).hasSize(3);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelConsumption).containsOnly(new BigDecimal("3500.690"), new BigDecimal("4500.379"), new BigDecimal("5500.613"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getEmissions).containsOnly(new BigDecimal("11027.174"), new BigDecimal("13951.175"), new BigDecimal("17051.900"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.JET_GASOLINE, AviationAerUkEtsFuelType.AVIATION_GASOLINE);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFlightsNumber).containsOnly(50, 70, 90);
        assertThat(result.getTotalEmissions()).isEqualTo(new BigDecimal("42030.249"));
    }

    @Test
    void calculateDomesticFlightsEmissions_not_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country3")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(50)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(60)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport11")
                                                .icao("ICAO11")
                                                .country("Country11")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3700.375"))
                                        .flightsNumber(70)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport12")
                                                .icao("ICAO12")
                                                .country("Country12")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3800.743"))
                                        .flightsNumber(80)
                                        .build()
                        ))
                        .build())
                .build();

        AviationAerDomesticFlightsEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
        final List<AviationAerDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = result.getDomesticFlightsEmissionsDetails();
        assertThat(domesticFlightsEmissionsDetails).hasSize(2);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelConsumption).containsOnly(new BigDecimal("6000.613"), new BigDecimal("7501.069"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getEmissions).containsOnly(new BigDecimal("18901.931"), new BigDecimal("23253.314"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.JET_GASOLINE);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFlightsNumber).containsOnly(90, 120);
        assertThat(result.getTotalEmissions()).isEqualTo(new BigDecimal("42155.245"));
    }

    @Test
    void calculateNonDomesticFlightsEmissions_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country1")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country2")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EFTA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EFTA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(50)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(60)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport11")
                                                .icao("ICAO11")
                                                .country("Country11")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.THIRD_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3700.375"))
                                        .flightsNumber(70)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport12")
                                                .icao("ICAO12")
                                                .country("Country12")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3800.743"))
                                        .flightsNumber(80)
                                        .build()
                        ))
                        .build())
                .build();

        AviationAerNonDomesticFlightsEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateNonDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
        final List<AviationAerNonDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = result.getNonDomesticFlightsEmissionsDetails();
        assertThat(domesticFlightsEmissionsDetails).hasSize(5);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelConsumption).containsOnly(new BigDecimal("2500.357"), new BigDecimal("2000.345"), new BigDecimal("2500.567"), new BigDecimal("3500.268"), new BigDecimal("3700.375"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getEmissions).containsOnly(new BigDecimal("7876.125"), new BigDecimal("6201.070"), new BigDecimal("7876.786"), new BigDecimal("10850.831"), new BigDecimal("11471.163"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.AVIATION_GASOLINE);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFlightsNumber).containsOnly(30, 30, 40, 60, 70);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getDepartureCountry).containsOnly("Country1", "Country5", "Country9", "Country11");
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getArrivalCountry).containsOnly("Country2", "Country6", "Country10");
        assertThat(result.getTotalEmissions()).isEqualTo(new BigDecimal("44275.975"));
    }

    @Test
    void calculateNonDomesticFlightsEmissions_not_all_fuel_types() {

        AviationAerEmissionsCalculationDTO aviationAerEmissionsCalculationDTO = AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                        .aggregatedEmissionDataDetails(Set.of(
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport1")
                                                .icao("ICAO1")
                                                .country("Country1")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport2")
                                                .icao("ICAO2")
                                                .country("Country2")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("1000.123"))
                                        .flightsNumber(10)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport3")
                                                .icao("ICAO3")
                                                .country("Country3")
                                                .countryType(CountryType.EFTA_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport4")
                                                .icao("ICAO4")
                                                .country("Country4")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("1500.234"))
                                        .flightsNumber(20)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.THIRD_COUNTRY)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("2000.345"))
                                        .flightsNumber(30)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport5")
                                                .icao("ICAO5")
                                                .country("Country5")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport6")
                                                .icao("ICAO6")
                                                .country("Country6")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("2500.567"))
                                        .flightsNumber(40)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport7")
                                                .icao("ICAO7")
                                                .country("Country7")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport8")
                                                .icao("ICAO8")
                                                .country("Country8")
                                                .countryType(CountryType.THIRD_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                        .fuelConsumption(new BigDecimal("3000.145"))
                                        .flightsNumber(50)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.EEA_COUNTRY)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3500.268"))
                                        .flightsNumber(60)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport11")
                                                .icao("ICAO11")
                                                .country("Country11")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport10")
                                                .icao("ICAO10")
                                                .country("Country10")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3700.375"))
                                        .flightsNumber(70)
                                        .build(),
                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                .name("Airport9")
                                                .icao("ICAO9")
                                                .country("Country9")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .airportTo(AviationRptAirportsDTO.builder()
                                                .name("Airport12")
                                                .icao("ICAO12")
                                                .country("Country12")
                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                .build())
                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                        .fuelConsumption(new BigDecimal("3800.743"))
                                        .flightsNumber(80)
                                        .build()
                        ))
                        .build())
                .build();

        AviationAerNonDomesticFlightsEmissions result = aviationAerUkEtsEmissionsCalculationService
                .calculateNonDomesticFlightsEmissions(aviationAerEmissionsCalculationDTO);
        final List<AviationAerNonDomesticFlightsEmissionsDetails> domesticFlightsEmissionsDetails = result.getNonDomesticFlightsEmissionsDetails();
        assertThat(domesticFlightsEmissionsDetails).hasSize(5);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelConsumption).containsOnly(new BigDecimal("1000.123"), new BigDecimal("1500.234"), new BigDecimal("2000.345"), new BigDecimal("3000.145"), new BigDecimal("3500.268"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getEmissions).containsOnly(new BigDecimal("3150.387"), new BigDecimal("4650.725"), new BigDecimal("6301.087"), new BigDecimal("9450.457"), new BigDecimal("10850.831"));
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFuelType).containsOnly(AviationAerUkEtsFuelType.JET_KEROSENE, AviationAerUkEtsFuelType.JET_GASOLINE);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getFlightsEmissionsDetails).extracting(AviationAerFlightsEmissionsDetails::getFlightsNumber).containsOnly(10, 20, 30, 50, 60);
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getDepartureCountry).containsOnly("Country1", "Country3", "Country5", "Country7", "Country9");
        assertThat(domesticFlightsEmissionsDetails).extracting(AviationAerNonDomesticFlightsEmissionsDetails::getArrivalCountry).containsOnly("Country2", "Country4", "Country6", "Country8", "Country10");
        assertThat(result.getTotalEmissions()).isEqualTo(new BigDecimal("34403.487"));
    }

    private AviationAerEmissionsCalculationDTO createEmissionCalculationDto() {
        Set<AviationAerUkEtsAggregatedEmissionDataDetails> aggregatedEmissionDataDetails = new HashSet<>();
        aggregatedEmissionDataDetails.add(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                .fuelConsumption(new BigDecimal(1000))
                .flightsNumber(10)
                .build()
        );
        aggregatedEmissionDataDetails.add(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
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
                .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                .fuelConsumption(new BigDecimal(2000))
                .flightsNumber(20)
                .build()
        );

        AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(aggregatedEmissionDataDetails)
                .build();

        AviationAerSafDetails safDetails = AviationAerSafDetails.builder()
                .totalSafMass(new BigDecimal(1500))
                .emissionsFactor(new BigDecimal("3.15"))
                .totalEmissionsReductionClaim(new BigDecimal(4725))
                .build();

        AviationAerSaf saf = AviationAerSaf.builder()
                .exist(true)
                .safDetails(safDetails)
                .build();

        return AviationAerEmissionsCalculationDTO.builder()
                .aggregatedEmissionsData(aggregatedEmissionsData)
                .saf(saf)
                .build();
    }
}

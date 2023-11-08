package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGaps;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach.AviationAerFuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsDataGapsSectionValidatorTest {

    @InjectMocks
    private AviationAerUkEtsDataGapsSectionValidator validator;

    @Test
    void validate_valid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                        Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom1")
                                                                .name("nameFrom1")
                                                                .country("countryFrom1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo1")
                                                                .name("nameTo1")
                                                                .country("countryTo1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.45))
                                                        .flightsNumber(14)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom2")
                                                                .name("nameFrom2")
                                                                .country("countryFrom2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo2")
                                                                .name("nameTo2")
                                                                .country("countryTo2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.56))
                                                        .flightsNumber(25)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom3")
                                                                .name("nameFrom3")
                                                                .country("countryFrom3")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo3")
                                                                .name("nameTo3")
                                                                .country("countryTo3")
                                                                .countryType(CountryType.EEA_COUNTRY)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.78))
                                                        .flightsNumber(12)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom4")
                                                                .name("nameFrom4")
                                                                .country("countryFrom4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo4")
                                                                .name("nameTo4")
                                                                .country("countryTo4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.67))
                                                        .flightsNumber(48)
                                                        .build())
                                )
                                .build())
                        .dataGaps(AviationAerDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGaps(List.of(
                                        createDataGap("reason", "type", "replacement method", 3, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 2, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 4, BigDecimal.TEN)
                                ))
                                .affectedFlightsPercentage(BigDecimal.valueOf(9.1))
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_invalid_affected_flights_percentage() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                        Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom1")
                                                                .name("nameFrom1")
                                                                .country("countryFrom1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo1")
                                                                .name("nameTo1")
                                                                .country("countryTo1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.45))
                                                        .flightsNumber(14)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom2")
                                                                .name("nameFrom2")
                                                                .country("countryFrom2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo2")
                                                                .name("nameTo2")
                                                                .country("countryTo2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.56))
                                                        .flightsNumber(25)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom3")
                                                                .name("nameFrom3")
                                                                .country("countryFrom3")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo3")
                                                                .name("nameTo3")
                                                                .country("countryTo3")
                                                                .countryType(CountryType.EEA_COUNTRY)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.78))
                                                        .flightsNumber(12)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom4")
                                                                .name("nameFrom4")
                                                                .country("countryFrom4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo4")
                                                                .name("nameTo4")
                                                                .country("countryTo4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.67))
                                                        .flightsNumber(48)
                                                        .build())
                                )
                                .build())
                        .dataGaps(AviationAerDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGaps(List.of(
                                        createDataGap("reason", "type", "replacement method", 3, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 2, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 4, BigDecimal.TEN)
                                ))
                                .affectedFlightsPercentage(BigDecimal.TEN)
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE.getMessage());
    }

    @Test
    void validate_no_data_gaps_exist_valid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                        Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom1")
                                                                .name("nameFrom1")
                                                                .country("countryFrom1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo1")
                                                                .name("nameTo1")
                                                                .country("countryTo1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.45))
                                                        .flightsNumber(14)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom2")
                                                                .name("nameFrom2")
                                                                .country("countryFrom2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo2")
                                                                .name("nameTo2")
                                                                .country("countryTo2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.56))
                                                        .flightsNumber(25)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom3")
                                                                .name("nameFrom3")
                                                                .country("countryFrom3")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo3")
                                                                .name("nameTo3")
                                                                .country("countryTo3")
                                                                .countryType(CountryType.EEA_COUNTRY)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.78))
                                                        .flightsNumber(12)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom4")
                                                                .name("nameFrom4")
                                                                .country("countryFrom4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo4")
                                                                .name("nameTo4")
                                                                .country("countryTo4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.67))
                                                        .flightsNumber(48)
                                                        .build())
                                )
                                .build())
                        .dataGaps(AviationAerDataGaps.builder()
                                .exist(Boolean.FALSE)
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_when_fuel_use_data_gaps_null_invalid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                        Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom1")
                                                                .name("nameFrom1")
                                                                .country("countryFrom1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo1")
                                                                .name("nameTo1")
                                                                .country("countryTo1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.45))
                                                        .flightsNumber(14)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom2")
                                                                .name("nameFrom2")
                                                                .country("countryFrom2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo2")
                                                                .name("nameTo2")
                                                                .country("countryTo2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.56))
                                                        .flightsNumber(25)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom3")
                                                                .name("nameFrom3")
                                                                .country("countryFrom3")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo3")
                                                                .name("nameTo3")
                                                                .country("countryTo3")
                                                                .countryType(CountryType.EEA_COUNTRY)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.78))
                                                        .flightsNumber(12)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom4")
                                                                .name("nameFrom4")
                                                                .country("countryFrom4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo4")
                                                                .name("nameTo4")
                                                                .country("countryTo4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.67))
                                                        .flightsNumber(48)
                                                        .build())
                                )
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_DATA_GAPS.getMessage());
    }

    @Test
    void validate_when_small_emitters_data_gaps_exist_invalid() {

        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .monitoringApproach(AviationAerFuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                        Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom1")
                                                                .name("nameFrom1")
                                                                .country("countryFrom1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo1")
                                                                .name("nameTo1")
                                                                .country("countryTo1")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.45))
                                                        .flightsNumber(14)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom2")
                                                                .name("nameFrom2")
                                                                .country("countryFrom2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo2")
                                                                .name("nameTo2")
                                                                .country("countryTo2")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(123.56))
                                                        .flightsNumber(25)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom3")
                                                                .name("nameFrom3")
                                                                .country("countryFrom3")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo3")
                                                                .name("nameTo3")
                                                                .country("countryTo3")
                                                                .countryType(CountryType.EEA_COUNTRY)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_KEROSENE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.78))
                                                        .flightsNumber(12)
                                                        .build(),
                                                AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                                                        .airportFrom(AviationRptAirportsDTO.builder()
                                                                .icao("icaoFrom4")
                                                                .name("nameFrom4")
                                                                .country("countryFrom4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                                                                .build())
                                                        .airportTo(AviationRptAirportsDTO.builder()
                                                                .icao("icaoTo4")
                                                                .name("nameTo4")
                                                                .country("countryTo4")
                                                                .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                                                                .build())
                                                        .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                                                        .fuelConsumption(BigDecimal.valueOf(234.67))
                                                        .flightsNumber(48)
                                                        .build())
                                )
                                .build())
                        .dataGaps(AviationAerDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGaps(List.of(
                                        createDataGap("reason", "type", "replacement method", 3, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 2, BigDecimal.TEN),
                                        createDataGap("reason", "type", "replacement method", 4, BigDecimal.TEN)
                                ))
                                .affectedFlightsPercentage(BigDecimal.valueOf(9.1))
                                .build())
                        .build())
                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_DATA_GAPS.getMessage());
    }

    private AviationAerDataGap createDataGap(String reason, String type, String replacementMethod, Integer flightsAffected, BigDecimal totalEmissions) {
        return AviationAerDataGap.builder()
                .reason(reason)
                .type(type)
                .replacementMethod(replacementMethod)
                .flightsAffected(flightsAffected)
                .totalEmissions(totalEmissions)
                .build();
    }
}

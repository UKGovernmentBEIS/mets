package uk.gov.pmrv.api.aviationreporting.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.validation.AviationAerAggregatedEmissionsDataSectionCommonValidator;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsAggregatedEmissionsDataSectionValidatorTest {

    @InjectMocks
    private AviationAerUkEtsAggregatedEmissionsDataSectionValidator validator;

    @Mock
    private AviationAerAggregatedEmissionsDataSectionCommonValidator commonValidator;

    @Test
    void validate_valid() {

        String icaoFrom1 = "icaoFrom1";
        String icaoTo1 = "icaoTo1";
        String icaoFrom2 = "icaoFrom2";
        String icaoTo2 = "icaoTo2";
        String icaoFrom3 = "icaoFrom3";
        String icaoTo3 = "icaoTo3";
        String icaoFrom4 = "icaoFrom4";
        String icaoTo4 = "icaoTo4";
        String icaoFrom5 = "icaoFrom5";
        String icaoTo5 = "icaoTo5";


        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
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
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
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
                        .icao(icaoFrom4)
                        .name("nameFrom4")
                        .country("countryFrom4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo4)
                        .name("nameTo4")
                        .country("countryTo4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom5)
                        .name("nameFrom5")
                        .country("countryFrom5")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo5)
                        .name("nameTo5")
                        .country("countryTo5")
                        .countryType(CountryType.EFTA_COUNTRY)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(45.67))
                    .flightsNumber(34)
                    .build()
            );
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                    dataDetails
                                )
                                .build())
                        .build())
                .build();
        
        when(commonValidator.validate(dataDetails, aerContainer.getReportingYear())).thenReturn(AviationAerValidationResult.builder().valid(true).build());
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_invalid_departure_aerodrome_country_type() {

        String icaoFrom1 = "icaoFrom1";
        String icaoTo1 = "icaoTo1";
        String icaoFrom2 = "icaoFrom2";
        String icaoTo2 = "icaoTo2";
        String icaoFrom3 = "icaoFrom3";
        String icaoTo3 = "icaoTo3";
        String icaoFrom4 = "icaoFrom4";
        String icaoTo4 = "icaoTo4";


        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.THIRD_COUNTRY)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
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
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
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
                        .icao(icaoFrom4)
                        .name("nameFrom4")
                        .country("countryFrom4")
                        .countryType(CountryType.EEA_DEPENDENT_COUNTRY)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo4)
                        .name("nameTo4")
                        .country("countryTo4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build());
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                    dataDetails
                                )
                                .build())
                        .build())
                .build();

        when(commonValidator.validate(dataDetails, aerContainer.getReportingYear())).thenReturn(AviationAerValidationResult.builder().valid(true).build());
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_AIRPORT_COUNTRY_TYPE.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(2);
    }

    @Test
    void validate_invalid_arrival_aerodrome_country_type_when_UK_departure() {

        String icaoFrom1 = "icaoFrom1";
        String icaoTo1 = "icaoTo1";
        String icaoFrom2 = "icaoFrom2";
        String icaoTo2 = "icaoTo2";
        String icaoFrom3 = "icaoFrom3";
        String icaoTo3 = "icaoTo3";
        String icaoFrom4 = "icaoFrom4";
        String icaoTo4 = "icaoTo4";
        String icaoFrom5 = "icaoFrom5";
        String icaoTo5 = "icaoTo5";


        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
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
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
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
                        .icao(icaoFrom4)
                        .name("nameFrom4")
                        .country("countryFrom4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo4)
                        .name("nameTo4")
                        .country("countryTo4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom5)
                        .name("nameFrom5")
                        .country("countryFrom5")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo5)
                        .name("nameTo5")
                        .country("countryTo5")
                        .countryType(CountryType.THIRD_COUNTRY)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build()
            );
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                    dataDetails
                                )
                                .build())
                        .build())
                .build();

        when(commonValidator.validate(dataDetails, aerContainer.getReportingYear())).thenReturn(AviationAerValidationResult.builder().valid(true).build());
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_REPORTED_DEPARTURE.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(1);
    }

    @Test
    void validate_invalid_arrival_aerodrome_country_type_when_Gibraltar_departure() {

        String icaoFrom1 = "icaoFrom1";
        String icaoTo1 = "icaoTo1";
        String icaoFrom2 = "icaoFrom2";
        String icaoTo2 = "icaoTo2";
        String icaoFrom3 = "icaoFrom3";
        String icaoTo3 = "icaoTo3";
        String icaoFrom4 = "icaoFrom4";
        String icaoTo4 = "icaoTo4";
        String icaoFrom5 = "icaoFrom5";
        String icaoTo5 = "icaoTo5";


        final Set<AviationAerUkEtsAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
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
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
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
                        .icao(icaoFrom4)
                        .name("nameFrom4")
                        .country("countryFrom4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo4)
                        .name("nameTo4")
                        .country("countryTo4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom5)
                        .name("nameFrom5")
                        .country("countryFrom5")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo5)
                        .name("nameTo5")
                        .country("countryTo5")
                        .countryType(CountryType.EEA_COUNTRY)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(234.67))
                    .flightsNumber(45)
                    .build()
            );
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .aer(AviationAerUkEts.builder()
                        .aggregatedEmissionsData(AviationAerUkEtsAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(
                                    dataDetails
                                )
                                .build())
                        .build())
                .build();

        when(commonValidator.validate(dataDetails, aerContainer.getReportingYear())).thenReturn(AviationAerValidationResult.builder().valid(true).build());
        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_ARRIVAL_AIRPORT_COUNTRY_TYPE_UKETS_FLIGHTS_TO_EEA_NOT_REPORTED_DEPARTURE.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(1);
    }
}

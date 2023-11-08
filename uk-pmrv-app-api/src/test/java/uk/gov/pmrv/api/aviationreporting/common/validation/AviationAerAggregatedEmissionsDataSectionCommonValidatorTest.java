package uk.gov.pmrv.api.aviationreporting.common.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationRptAirportsService;

@ExtendWith(MockitoExtension.class)
class AviationAerAggregatedEmissionsDataSectionCommonValidatorTest {

    @InjectMocks
    private AviationAerAggregatedEmissionsDataSectionCommonValidator validator;

    @Mock
    private AviationRptAirportsService airportsService;

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

        when(airportsService.getAirportsByIcaoCodesAndYear(Mockito.anySet(), any())).thenReturn(List.of(
                createAirport(icaoFrom1, "nameFrom1", "countryFrom1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom2, "nameFrom2", "countryFrom2", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom3, "nameFrom3", "countryFrom3", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom4, "nameFrom4", "countryFrom4", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoFrom5, "nameFrom5", "countryFrom5", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo1, "nameTo1", "countryTo1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo2, "nameTo2", "countryTo2", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoTo3, "nameTo3", "countryTo3", CountryType.EEA_COUNTRY),
                createAirport(icaoTo4, "nameTo4", "countryTo4", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo5, "nameTo5", "countryTo5", CountryType.EFTA_COUNTRY)
        ));
        final AviationAerValidationResult actual = validator.validate(dataDetails, Year.of(2022));
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
    }

    @Test
    void validate_icao_codes_not_found_invalid() {

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
                        .icao("invalid_icao")
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
                    .build());

        when(airportsService.getAirportsByIcaoCodesAndYear(Mockito.anySet(), any())).thenReturn(List.of(
                createAirport(icaoFrom1, "nameFrom1", "countryFrom1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom3, "nameFrom3", "countryFrom3", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom4, "nameFrom4", "countryFrom4", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoFrom5, "nameFrom5", "countryFrom5", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo1, "nameTo1", "countryTo1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo2, "nameTo2", "countryTo2", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoTo3, "nameTo3", "countryTo3", CountryType.EEA_COUNTRY),
                createAirport(icaoTo4, "nameTo4", "countryTo4", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo5, "nameTo5", "countryTo5", CountryType.EFTA_COUNTRY)
        ));
        final AviationAerValidationResult actual = validator.validate(dataDetails, Year.of(2022));
        assertFalse(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_ICAO_CODE.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(2);

    }

    @Test
    void validate_invalid_country_types() {

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
                    .build(),
                    AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo4)
                        .name("nameTo4")
                        .country("countryTo4")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .build())
                    .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build()
            );

        when(airportsService.getAirportsByIcaoCodesAndYear(Mockito.anySet(), any())).thenReturn(List.of(
                createAirport(icaoFrom1, "nameFrom1", "countryFrom1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom2, "nameFrom2", "countryFrom2", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom3, "nameFrom3", "countryFrom3", CountryType.EFTA_COUNTRY),
                createAirport(icaoFrom4, "nameFrom4", "countryFrom4", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoFrom5, "nameFrom5", "countryFrom5", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo1, "nameTo1", "countryTo1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo2, "nameTo2", "countryTo2", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoTo3, "nameTo3", "countryTo3", CountryType.EEA_COUNTRY),
                createAirport(icaoTo4, "nameTo4", "countryTo4", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo5, "nameTo5", "countryTo5", CountryType.EEA_COUNTRY)
        ));
        final AviationAerValidationResult actual = validator.validate(dataDetails, Year.of(2022));
        assertFalse(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_COUNTRY_TYPE.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(2);
    }
    
    @Test
    void validate_same_aerodromes_invalid() {

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
                        .icao(icaoFrom1)
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
                    .build()
            );

        when(airportsService.getAirportsByIcaoCodesAndYear(Mockito.anySet(), any())).thenReturn(List.of(
                createAirport(icaoFrom1, "nameFrom1", "countryFrom1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom2, "nameFrom2", "countryFrom2", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom3, "nameFrom3", "countryFrom3", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoFrom4, "nameFrom4", "countryFrom4", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoFrom5, "nameFrom5", "countryFrom5", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo1, "nameTo1", "countryTo1", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo2, "nameTo2", "countryTo2", CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED),
                createAirport(icaoTo3, "nameTo3", "countryTo3", CountryType.EEA_COUNTRY),
                createAirport(icaoTo4, "nameTo4", "countryTo4", CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED),
                createAirport(icaoTo5, "nameTo5", "countryTo5", CountryType.EFTA_COUNTRY)
        ));
        final AviationAerValidationResult actual = validator.validate(dataDetails, Year.of(2022));
        assertTrue(actual.isValid());
        assertEquals(1, actual.getAerViolations().size());
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_ARRIVAL_AIRPORTS.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(1);
    }

    private AviationRptAirportsDTO createAirport(String icao, String name, String country, CountryType countryType) {

        return AviationRptAirportsDTO.builder()
                .icao(icao)
                .name(name)
                .country(country)
                .countryType(countryType)
                .build();
    }
}

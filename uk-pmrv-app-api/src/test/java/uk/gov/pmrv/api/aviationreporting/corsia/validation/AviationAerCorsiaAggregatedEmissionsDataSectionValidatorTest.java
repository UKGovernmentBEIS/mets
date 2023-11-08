package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Set;
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
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaAggregatedEmissionsDataSectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaAggregatedEmissionsDataSectionValidator validator;

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
        
        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state2")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
                        .name("nameTo2")
                        .country("countryTo2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .state("state2")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.56))
                    .flightsNumber(25)
                    .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
                        .name("nameTo3")
                        .country("countryTo3")
                        .countryType(CountryType.EEA_COUNTRY)
                        .state("state2")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                    .fuelConsumption(BigDecimal.valueOf(234.78))
                    .flightsNumber(12)
                    .build()
            );
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingYear(Year.of(2022))
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
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
    void validate_whenSameState_thenInvalid() {

        String icaoFrom1 = "icaoFrom1";
        String icaoTo1 = "icaoTo1";
        String icaoFrom2 = "icaoFrom2";
        String icaoTo2 = "icaoTo2";
        String icaoFrom3 = "icaoFrom3";
        String icaoTo3 = "icaoTo3";


        final Set<AviationAerCorsiaAggregatedEmissionDataDetails> dataDetails =
            Set.of(AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom1)
                        .name("nameFrom1")
                        .country("countryFrom1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo1)
                        .name("nameTo1")
                        .country("countryTo1")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.AVIATION_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.45))
                    .flightsNumber(10)
                    .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom2)
                        .name("nameFrom2")
                        .country("countryFrom2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo2)
                        .name("nameTo2")
                        .country("countryTo2")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED)
                        .state("state2")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.JET_GASOLINE)
                    .fuelConsumption(BigDecimal.valueOf(123.56))
                    .flightsNumber(25)
                    .build(),
                    AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                    .airportFrom(AviationRptAirportsDTO.builder()
                        .icao(icaoFrom3)
                        .name("nameFrom3")
                        .country("countryFrom3")
                        .countryType(CountryType.UKETS_FLIGHTS_TO_EEA_REPORTED)
                        .state("state1")
                        .build())
                    .airportTo(AviationRptAirportsDTO.builder()
                        .icao(icaoTo3)
                        .name("nameTo3")
                        .country("countryTo3")
                        .countryType(CountryType.EEA_COUNTRY)
                        .state("state2")
                        .build())
                    .fuelType(AviationAerCorsiaFuelType.JET_KEROSENE)
                    .fuelConsumption(BigDecimal.valueOf(234.78))
                    .flightsNumber(12)
                    .build()
            );
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .reportingYear(Year.of(2022))
            .aer(AviationAerCorsia.builder()
                .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
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
            .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_DEPARTURE_ARRIVAL_STATES.getMessage());
        final Object[] data = actual.getAerViolations().get(0).getData();
        assertThat(data).hasSize(1);
    }
}

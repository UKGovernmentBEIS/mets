package uk.gov.pmrv.api.aviationreporting.corsia.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerValidationResult;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerViolation;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGaps;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGapsDetails;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.datagaps.AviationAerCorsiaDataGapsPercentageType;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaSubmittedEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.datagaps.AviationAerDataGap;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaDataGapsSectionValidatorTest {

    @InjectMocks
    private AviationAerCorsiaDataGapsSectionValidator validator;

    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService emissionsCalculationService;
    
    @Test
    void validate_less_than_5_percent_valid() {
        Set<AviationAerCorsiaAggregatedEmissionDataDetails> aviationAerCorsiaAggregatedEmissionDataDetails = new HashSet<>();
        AviationAerCorsiaAggregatedEmissionDataDetails details1 =
                createAggregatedEmissionDataDetails(createAirport("icao1", "name1", "country1"), createAirport("icao2", "name2", "country2"), AviationAerCorsiaFuelType.JET_GASOLINE, 7, BigDecimal.TEN);
        AviationAerCorsiaAggregatedEmissionDataDetails details2 =
                createAggregatedEmissionDataDetails(createAirport("icao3", "name3", "country3"), createAirport("icao4", "name4", "country4"), AviationAerCorsiaFuelType.JET_KEROSENE, 5, BigDecimal.valueOf(20));
        AviationAerCorsiaAggregatedEmissionDataDetails details3 =
                createAggregatedEmissionDataDetails(createAirport("icao5", "name5", "country5"), createAirport("icao6", "name6", "country6"), AviationAerCorsiaFuelType.JET_KEROSENE, 9, BigDecimal.valueOf(30));
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details1);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details2);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details3);

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(aviationAerCorsiaAggregatedEmissionDataDetails)
                                .build())
                        .dataGaps(AviationAerCorsiaDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                                		.dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.LESS_EQUAL_FIVE_PER_CENT)
                                		.dataGapsPercentage(BigDecimal.valueOf(1))
                                        .build())
                                .build())
                        .build())

                .build();

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
        verifyNoInteractions(emissionsCalculationService);
    }
    
    @Test
    void validate_more_than_5_percent_valid() {
        Set<AviationAerCorsiaAggregatedEmissionDataDetails> aviationAerCorsiaAggregatedEmissionDataDetails = new HashSet<>();
        AviationAerCorsiaAggregatedEmissionDataDetails details1 =
                createAggregatedEmissionDataDetails(createAirport("icao1", "name1", "country1"), createAirport("icao2", "name2", "country2"), AviationAerCorsiaFuelType.JET_GASOLINE, 7, BigDecimal.TEN);
        AviationAerCorsiaAggregatedEmissionDataDetails details2 =
                createAggregatedEmissionDataDetails(createAirport("icao3", "name3", "country3"), createAirport("icao4", "name4", "country4"), AviationAerCorsiaFuelType.JET_KEROSENE, 5, BigDecimal.valueOf(20));
        AviationAerCorsiaAggregatedEmissionDataDetails details3 =
                createAggregatedEmissionDataDetails(createAirport("icao5", "name5", "country5"), createAirport("icao6", "name6", "country6"), AviationAerCorsiaFuelType.JET_KEROSENE, 9, BigDecimal.valueOf(30));
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details1);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details2);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details3);

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(aviationAerCorsiaAggregatedEmissionDataDetails)
                                .build())
                        .dataGaps(AviationAerCorsiaDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                                		.dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                                        .dataGaps(List.of(
                                                createDataGap("reason", "type", "replacement method", 3, BigDecimal.TEN),
                                                createDataGap("reason", "type", "replacement method", 2, BigDecimal.TEN),
                                                createDataGap("reason", "type", "replacement method", 4, BigDecimal.TEN)
                                        ))
                                        .affectedFlightsPercentage(BigDecimal.valueOf(56.3))
                                        .build())
                                .build())
                        .build())

                .build();

        when(emissionsCalculationService.findOffsettingFlights(aviationAerCorsiaAggregatedEmissionDataDetails)).thenReturn(Set.of(details1, details3));

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getAerViolations()).isEmpty();
        verify(emissionsCalculationService, times(1)).findOffsettingFlights(aviationAerCorsiaAggregatedEmissionDataDetails);
    }
    
    @Test
    void validate_more_than_5_percent_invalid() {
        Set<AviationAerCorsiaAggregatedEmissionDataDetails> aviationAerCorsiaAggregatedEmissionDataDetails = new HashSet<>();
        AviationAerCorsiaAggregatedEmissionDataDetails details1 =
                createAggregatedEmissionDataDetails(createAirport("icao1", "name1", "country1"), createAirport("icao2", "name2", "country2"), AviationAerCorsiaFuelType.JET_GASOLINE, 7, BigDecimal.TEN);
        AviationAerCorsiaAggregatedEmissionDataDetails details2 =
                createAggregatedEmissionDataDetails(createAirport("icao3", "name3", "country3"), createAirport("icao4", "name4", "country4"), AviationAerCorsiaFuelType.JET_KEROSENE, 5, BigDecimal.valueOf(20));
        AviationAerCorsiaAggregatedEmissionDataDetails details3 =
                createAggregatedEmissionDataDetails(createAirport("icao5", "name5", "country5"), createAirport("icao6", "name6", "country6"), AviationAerCorsiaFuelType.JET_KEROSENE, 9, BigDecimal.valueOf(30));
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details1);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details2);
        aviationAerCorsiaAggregatedEmissionDataDetails.add(details3);

        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .aer(AviationAerCorsia.builder()
                        .aggregatedEmissionsData(AviationAerCorsiaAggregatedEmissionsData.builder()
                                .aggregatedEmissionDataDetails(aviationAerCorsiaAggregatedEmissionDataDetails)
                                .build())
                        .dataGaps(AviationAerCorsiaDataGaps.builder()
                                .exist(Boolean.TRUE)
                                .dataGapsDetails(AviationAerCorsiaDataGapsDetails.builder()
                                		.dataGapsPercentageType(AviationAerCorsiaDataGapsPercentageType.MORE_THAN_FIVE_PER_CENT)
                                        .dataGaps(List.of(
                                                createDataGap("reason", "type", "replacement method", 3, BigDecimal.TEN),
                                                createDataGap("reason", "type", "replacement method", 2, BigDecimal.TEN),
                                                createDataGap("reason", "type", "replacement method", 4, BigDecimal.TEN)
                                        ))
                                        .affectedFlightsPercentage(BigDecimal.TEN)
                                        .build())
                                .build())
                        .build())

                .build();

        when(emissionsCalculationService.findOffsettingFlights(aviationAerCorsiaAggregatedEmissionDataDetails)).thenReturn(Set.of(details1, details3));

        final AviationAerValidationResult actual = validator.validate(aerContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getAerViolations()).hasSize(1);
        assertThat(actual.getAerViolations()).extracting(AviationAerViolation::getMessage)
                .containsOnly(AviationAerViolation.AviationAerViolationMessage.INVALID_AFFECTED_FLIGHTS_PERCENTAGE.getMessage());
        
        verify(emissionsCalculationService, times(1)).findOffsettingFlights(aviationAerCorsiaAggregatedEmissionDataDetails);
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

    private AviationAerCorsiaAggregatedEmissionDataDetails createAggregatedEmissionDataDetails(AviationRptAirportsDTO airportFrom,
                                                                                               AviationRptAirportsDTO airportTo,
                                                                                               AviationAerCorsiaFuelType fuelType,
                                                                                               Integer flightsNumber,
                                                                                               BigDecimal fuelConsumption) {

        return AviationAerCorsiaAggregatedEmissionDataDetails.builder()
                .airportFrom(airportFrom)
                .airportTo(airportTo)
                .fuelType(fuelType)
                .flightsNumber(flightsNumber)
                .fuelConsumption(fuelConsumption)
                .build();
    }

    private AviationRptAirportsDTO createAirport(String icao, String name, String country) {
        return AviationRptAirportsDTO.builder()
                .icao(icao)
                .name(name)
                .country(country)
                .build();
    }
}

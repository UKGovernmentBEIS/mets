package uk.gov.pmrv.api.aviationreporting.ukets.domain.aggregatedemissionsdata;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;
import uk.gov.pmrv.api.aviationreporting.common.enumeration.CountryType;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionDataDetails;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerAggregatedEmissionsDataTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                                .icao("icaoFrom")
                                .name("nameFrom")
                                .country("countryFrom")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("icaoTo")
                                .name("nameTo")
                                .country("countryTo")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(BigDecimal.valueOf(123.45))
                        .flightsNumber(10)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_no_fuel_type_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                                .icao("icaoFrom")
                                .name("nameFrom")
                                .country("countryFrom")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("icaoTo")
                                .name("nameTo")
                                .country("countryTo")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelConsumption(BigDecimal.valueOf(123.456))
                        .flightsNumber(10)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_zero_flights_zero_fuel_consumption_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                                .icao("icaoFrom")
                                .name("nameFrom")
                                .country("countryFrom")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")                            
                                .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("icaoTo")
                                .name("nameTo")
                                .country("countryTo")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(BigDecimal.ZERO)
                        .flightsNumber(0)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(2, violations.size());
    }

    @Test
    void validate_more_than_3_decimals_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                                .icao("icaoFrom")
                                .name("nameFrom")
                                .country("countryFrom")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")                            
                                .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("icaoTo")
                                .name("nameTo")
                                .country("countryTo")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(BigDecimal.valueOf(123.4567))
                        .flightsNumber(200)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }

    @Test
    void validate_no_airport_from_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder().state("state").build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("icaoTo")
                                .name("nameTo")
                                .country("countryTo")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(BigDecimal.valueOf(123.45))
                        .flightsNumber(10)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(4, violations.size());
    }

    @Test
    void validate_blank_values_on_airport_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of(AviationAerUkEtsAggregatedEmissionDataDetails.builder()
                        .airportFrom(AviationRptAirportsDTO.builder()
                                .icao("icaoFrom")
                                .name("nameFrom")
                                .country("countryFrom")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .airportTo(AviationRptAirportsDTO.builder()
                                .icao("")
                                .name("")
                                .country("")
                                .countryType(CountryType.EEA_COUNTRY)
                                .state("state")
                                .build())
                        .fuelType(AviationAerUkEtsFuelType.AVIATION_GASOLINE)
                        .fuelConsumption(BigDecimal.valueOf(123.45))
                        .flightsNumber(10)
                        .build()))
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(3, violations.size());
    }

    @Test
    void validate_empty_aggregated_emissions_data_invalid() {

        final AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData = AviationAerUkEtsAggregatedEmissionsData.builder()
                .aggregatedEmissionDataDetails(Set.of())
                .build();

        final Set<ConstraintViolation<AviationAerUkEtsAggregatedEmissionsData>> violations = validator.validate(aggregatedEmissionsData);

        assertEquals(1, violations.size());
    }
}

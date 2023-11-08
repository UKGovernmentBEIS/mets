package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaMonitoringApproachTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_CERT_used_false_not_applicable_fuel_density_valid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.FALSE)
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.NOT_APPLICABLE)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_CERT_used_false_actual_standard_density_block_hour_false_valid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.FALSE)
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.FALSE)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_CERT_used_false_standard_density_block_hour_true_valid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.FALSE)
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.TRUE)
                        .aircraftTypeDetails(Set.of(AviationAerCorsiaAircraftTypeDetails.builder()
                                        .designator("designator")
                                        .subtype("subtype")
                                        .fuelBurnRatio(BigDecimal.valueOf(100.123))
                                .build()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_CERT_used_true_standard_density_block_hour_true_valid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.TRUE)
                        .aircraftTypeDetails(Set.of(AviationAerCorsiaAircraftTypeDetails.builder()
                                .designator("designator")
                                .subtype("subtype")
                                .fuelBurnRatio(BigDecimal.valueOf(100.123))
                                .build()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(0, violations.size());
    }

    @Test
    void when_CERT_used_false_cert_used_details_exist_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.FALSE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.TRUE)
                        .aircraftTypeDetails(Set.of(AviationAerCorsiaAircraftTypeDetails.builder()
                                .designator("designator")
                                .subtype("subtype")
                                .fuelBurnRatio(BigDecimal.valueOf(100.123))
                                .build()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.certUsed}");
    }

    @Test
    void when_CERT_used_true_cert_used_details_null_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.TRUE)
                        .aircraftTypeDetails(Set.of(AviationAerCorsiaAircraftTypeDetails.builder()
                                .designator("designator")
                                .subtype("subtype")
                                .fuelBurnRatio(BigDecimal.valueOf(100.123))
                                .build()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.certUsed}");
    }

    @Test
    void when_fuel_density_not_applicable_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.NOT_APPLICABLE)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.FALSE)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.identicalToProcedure}",
                        "{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.blockHourUsed}");
    }

    @Test
    void when_fuel_density_actual_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_DENSITY)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(2, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.identicalToProcedure}",
                        "{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.blockHourUsed}");
    }

    @Test
    void when_block_hour_used_true_aircrafts_exist_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.TRUE)
                        .blockHourUsed(Boolean.FALSE)
                        .aircraftTypeDetails(Set.of(AviationAerCorsiaAircraftTypeDetails.builder()
                                .designator("designator")
                                .subtype("subtype")
                                .fuelBurnRatio(BigDecimal.valueOf(100.123))
                                .build()))
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.aircraftTypeDetails}");
    }

    @Test
    void when_block_hour_used_false_aircrafts_empty_invalid() {

        final AviationAerCorsiaMonitoringApproach monitoringApproach = AviationAerCorsiaMonitoringApproach.builder()
                .certUsed(Boolean.TRUE)
                .certDetails(AviationAerCorsiaCertDetails.builder()
                        .flightType(AviationAerCorsiaFlightType.INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS)
                        .publicationYear(Year.of(2022))
                        .build())
                .fuelUseMonitoringDetails(AviationAerCorsiaFuelUseMonitoringDetails.builder()
                        .fuelDensityType(AviationAerCorsiaFuelDensityType.ACTUAL_STANDARD_DENSITY)
                        .identicalToProcedure(Boolean.FALSE)
                        .blockHourUsed(Boolean.TRUE)
                        .build())
                .build();

        final Set<ConstraintViolation<AviationAerCorsiaMonitoringApproach>> violations = validator.validate(monitoringApproach);

        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsOnly("{aviationAer.corsia.monitoringApproach.fuelUseMonitoringDetails.aircraftTypeDetails}");
    }
}

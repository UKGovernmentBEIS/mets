package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration.FuelTypeCorsia;

@ExtendWith(MockitoExtension.class)
public class EmpEmissionSourcesCorsiaTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_valid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build(),
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type 2")
                        .manufacturer("manufacturer 2")
                        .model("model 2")
                        .build())
                    .subtype("subtype")
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertEquals(0, violations.size());
    }

    @Test
    void validate_emptyAircraftTypes_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Collections.emptySet())
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
    }

    @Test
    void validate_nullFuelTypes_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .build() // fuelTypes is not set
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_blankManufacturer_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("") // blank manufacturer
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_subtypeExceedingMaxSize_invalid() {
        String longSubtype = String.join("", Collections.nCopies(10001, "a"));

        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .subtype(longSubtype)
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_nullNumberOfAircrafts_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build()  // numberOfAircrafts is not set
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_longMultipleFuelConsumptionMethodsExplanation_invalid() {
        String longExplanation = String.join("", Collections.nCopies(10001, "a"));

        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation(longExplanation)
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_blankModel_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("")  // blank model
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_negativeNumberOfAircrafts_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(-5L)  // Negative value
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_nullAircraftTypes_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(null)
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_emptyFuelTypes_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(new ArrayList<>())
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_fuelTypesContainsNull_invalid() {
        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(Arrays.asList(FuelTypeCorsia.JET_GASOLINE, null))
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }

    @Test
    void validate_longSubtype_invalid() {
        String longSubtype = String.join("", Collections.nCopies(10001, "a"));

        final EmpEmissionSourcesCorsia emissionSources = EmpEmissionSourcesCorsia.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetailsCorsia.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelTypeCorsia.JET_GASOLINE, FuelTypeCorsia.TS_1))
                    .subtype(longSubtype)
                    .build()
            ))
            .multipleFuelConsumptionMethodsExplanation("multiple fuel methods")
            .build();

        final Set<ConstraintViolation<EmpEmissionSourcesCorsia>> violations = validator.validate(emissionSources);

        assertFalse(violations.isEmpty());
    }
}

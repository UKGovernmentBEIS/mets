package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmissionSourcesTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_aircraft_types_empty_then_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of())
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(3, violations.size());
        assertThat(violations)
            .extracting(ConstraintViolation::getMessage)
            .containsExactlyInAnyOrder("must not be empty", "{emp.emissionSources.aircraftTypes}", "{emp.emissionSources.fuelTypes.otherFuelExplanation}");
    }

    @Test
    void when_aircraft_types_is_currently_in_use_false_then_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(false)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("{emp.emissionSources.aircraftTypes}");
    }

    @Test
    void when_aircraft_types_has_null_element_invalid() {
        Set<AircraftTypeDetails> aircraftTypeDetails = new HashSet<>();
        aircraftTypeDetails.add(AircraftTypeDetails.builder()
                .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                .numberOfAircrafts(10L)
                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                .isCurrentlyUsed(true)
                .build());
        aircraftTypeDetails.add(null);
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(aircraftTypeDetails)
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void when_aircraft_type_info_is_null_then_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(null)
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void when_aircraft_type_info_has_blank_fields_then_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("")
                                        .manufacturer("")
                                        .model("")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(3, violations.size());
        assertThat(violations).extracting(ConstraintViolation::getMessage)
                .contains("must not be blank");

    }


    @Test
    void when_operational_details_aircraft_number_out_of_range_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(-2L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must be greater than or equal to 0");
    }

    @Test
    void when_operational_details_fuels_is_empty_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of())
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must not be empty");
    }

    @Test
    void when_operational_details_fuels_null_invalid() {
        List<FuelType> fuelTypes = new ArrayList<>();
        fuelTypes.add(FuelType.JET_KEROSENE);
        fuelTypes.add(null);
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(fuelTypes)
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void when_is_currently_used_is_null_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .build(),
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type 2")
                                        .manufacturer("manufacturer 2")
                                        .model("model 2")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("must not be null");
    }

    @Test
    void validate_valid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE, FuelType.OTHER))
                                .isCurrentlyUsed(false)
                                .build(),
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type 2")
                                        .manufacturer("manufacturer 2")
                                        .model("model 2")
                                        .build())
                                .subtype("subtype")
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(true)
                                .build()
                ))
                .otherFuelExplanation("other fuel explanation")
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(0, violations.size());
    }

    @Test
    void when_is_currently_in_use_are_all_false_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
                .aircraftTypes(Set.of(
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type")
                                        .manufacturer("manufacturer")
                                        .model("model")
                                        .build())
                                .subtype("subtype")
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE, FuelType.AVIATION_GASOLINE))
                                .isCurrentlyUsed(false)
                                .build(),
                        AircraftTypeDetails.builder()
                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                        .designatorType("designator type 2")
                                        .manufacturer("manufacturer 2")
                                        .model("model 2")
                                        .build())
                                .subtype("subtype")
                                .numberOfAircrafts(10L)
                                .fuelTypes(List.of(FuelType.JET_GASOLINE))
                                .isCurrentlyUsed(false)
                                .build()
                ))
                .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("{emp.emissionSources.aircraftTypes}");
    }

    @Test
    void when_at_least_one_other_fuel_type_and_no_explanation_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetails.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .subtype("subtype")
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelType.JET_GASOLINE, FuelType.AVIATION_GASOLINE))
                    .isCurrentlyUsed(true)
                    .build(),
                AircraftTypeDetails.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type 2")
                        .manufacturer("manufacturer 2")
                        .model("model 2")
                        .build())
                    .subtype("subtype")
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelType.JET_GASOLINE, FuelType.OTHER))
                    .isCurrentlyUsed(false)
                    .build()
            ))
            .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("{emp.emissionSources.fuelTypes.otherFuelExplanation}");
    }

    @Test
    void when_no_other_fuel_type_and_explanation_exists_invalid() {
        final EmpEmissionSources emissionSources = EmpEmissionSources.builder()
            .aircraftTypes(Set.of(
                AircraftTypeDetails.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type")
                        .manufacturer("manufacturer")
                        .model("model")
                        .build())
                    .subtype("subtype")
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelType.JET_GASOLINE, FuelType.AVIATION_GASOLINE))
                    .isCurrentlyUsed(true)
                    .build(),
                AircraftTypeDetails.builder()
                    .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .designatorType("designator type 2")
                        .manufacturer("manufacturer 2")
                        .model("model 2")
                        .build())
                    .subtype("subtype")
                    .numberOfAircrafts(10L)
                    .fuelTypes(List.of(FuelType.JET_GASOLINE))
                    .isCurrentlyUsed(false)
                    .build()
            ))
            .otherFuelExplanation("explanation")
            .build();

        final Set<ConstraintViolation<EmpEmissionSources>> violations = validator.validate(emissionSources);

        assertEquals(1, violations.size());
        assertThat(new ArrayList<>(violations).get(0).getMessage()).isEqualTo("{emp.emissionSources.fuelTypes.otherFuelExplanation}");
    }
}

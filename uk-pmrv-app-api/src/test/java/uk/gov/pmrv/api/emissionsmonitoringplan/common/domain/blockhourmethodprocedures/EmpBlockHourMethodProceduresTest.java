package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhourmethodprocedures;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.FuelBurnCalculationType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmpBlockHourMethodProceduresTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void when_both_fuel_burn_calculation_types_then_valid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION, FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .notClearDistinguishionIcaoAircraftDesignators(Set.of("icao3", "icao4"))
                .assignmentAndAdjustment("assignment")
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isEmpty();
    }

    @Test
    void when_clear_distinguishion_fuel_burn_calculation_type_then_valid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION))
                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .assignmentAndAdjustment("assignment")
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isEmpty();
    }

    @Test
    void when_not_clear_distinguishion_fuel_burn_calculation_type_then_valid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                .notClearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isEmpty();
    }

    @Test
    void when_clear_distinguishion_fuel_burn_calculation_type_and_assignment_null_then_invalid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION))
                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isNotEmpty();
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{emp.blockHourMethodProcedures.assignmentAndAdjustment}");
    }

    @Test
    void when_not_clear_distinguishion_fuel_burn_calculation_type_and_assignment_exist_then_invalid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                .notClearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .assignmentAndAdjustment("assignment")
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isNotEmpty();
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{emp.blockHourMethodProcedures.assignmentAndAdjustment}");
    }

    @Test
    void when_empty_fuel_burn_calculation_types_then_invalid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of())
                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao1", "icao2"))
                .assignmentAndAdjustment("assignment")
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).hasSize(4);
    }

    @Test
    void when_blank_icao_designators_then_invalid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION, FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao1", ""))
                .notClearDistinguishionIcaoAircraftDesignators(Set.of("icao2", "icao3"))
                .assignmentAndAdjustment("assignment")
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).hasSize(1);
    }

    @Test
    void when_clear_distinguishion_fuel_burn_calculation_type_empty_designators_then_invalid() {
        EmpBlockHourMethodProcedures blockHourMethodProcedures = EmpBlockHourMethodProcedures.builder()
                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.NOT_CLEAR_DISTINGUISHION))
                .blockHoursMeasurement(createProcedureForm())
                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_DELIVERY_NOTES)
                .fuelDensity(createProcedureForm())
                .build();

        Set<ConstraintViolation<EmpBlockHourMethodProcedures>> violations = validator.validate(blockHourMethodProcedures);

        assertThat(violations).isNotEmpty();
        assertEquals(1, violations.size());
        assertThat(violations)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("{emp.blockHourMethodProcedures.notClearDistinguishionIcaoAircraftDesignators}");
    }

    private EmpProcedureForm createProcedureForm() {
        return EmpProcedureForm.builder()
                .procedureDescription("procedure description")
                .procedureDocumentName("procedure document name")
                .procedureReference("procedure reference")
                .responsibleDepartmentOrRole("responsible department")
                .locationOfRecords("location of records")
                .itSystemUsed("IT system")
                .build();
    }
}

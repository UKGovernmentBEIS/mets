package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.EmpBlockHourMethodProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.blockhour.FuelBurnCalculationType;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration.FuelUpliftSupplierRecordType;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.EmissionsMonitoringPlanCorsiaContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachTypeCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsmonitoringapproach.FuelMonitoringApproachCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.AircraftTypeDetailsCorsia;
import uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.EmpEmissionSourcesCorsia;

@ExtendWith(MockitoExtension.class)
class EmpCorsiaBlockHourMethodProceduresSectionValidatorTest {

    private final EmpCorsiaBlockHourMethodProceduresSectionValidator validator =
            new EmpCorsiaBlockHourMethodProceduresSectionValidator();

    @Test
    void validate_block_hour_method_valid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_HOUR)
                                                .build()))
                                .build())
                        .blockHourMethodProcedures(EmpBlockHourMethodProcedures.builder()
                                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION))
                                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao"))
                                .assignmentAndAdjustment("assignment")
                                .blockHoursMeasurement(createEmpProcedureForm())
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_no_block_hour_method_valid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build()))
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void when_block_hour_method_no_block_hour_method_procedures_invalid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_HOUR)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build()))
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_HOUR_METHOD_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpBlockHourMethodProcedures.class.getSimpleName());
    }

    @Test
    void when_no_block_hour_method_and_block_hour_method_procedures_exist_invalid() {

        final EmissionsMonitoringPlanCorsiaContainer empContainer = EmissionsMonitoringPlanCorsiaContainer.builder()
                .scheme(EmissionTradingScheme.CORSIA)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanCorsia.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproachCorsia.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachTypeCorsia.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSourcesCorsia.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetailsCorsia.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build()))
                                .build())
                        .blockHourMethodProcedures(EmpBlockHourMethodProcedures.builder()
                                .fuelBurnCalculationTypes(Set.of(FuelBurnCalculationType.CLEAR_DISTINGUISHION))
                                .clearDistinguishionIcaoAircraftDesignators(Set.of("icao"))
                                .assignmentAndAdjustment("assignment")
                                .blockHoursMeasurement(createEmpProcedureForm())
                                .fuelUpliftSupplierRecordType(FuelUpliftSupplierRecordType.FUEL_INVOICES)
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = validator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_BLOCK_HOUR_METHOD_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpBlockHourMethodProcedures.class.getSimpleName());
    }

    private EmpProcedureForm createEmpProcedureForm() {
        return EmpProcedureForm.builder()
                .procedureReference("procedure reference")
                .procedureDocumentName("procedure document name")
                .procedureDescription("procedure description")
                .responsibleDepartmentOrRole("responsible role")
                .locationOfRecords("records location")
                .itSystemUsed("IT system")
                .build();
    }
}

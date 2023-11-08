package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.methodb.EmpMethodBProcedures;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmpMethodBProceduresSectionValidatorTest {

    private final EmpMethodBProceduresSectionValidator methodBProceduresSectionValidator =
            new EmpMethodBProceduresSectionValidator();

    @Test
    void validate_method_B_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_B)
                                                .build()))
                                .build())
                        .methodBProcedures(EmpMethodBProcedures.builder()
                                .fuelConsumptionPerFlight(createEmpProcedureForm())
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = methodBProceduresSectionValidator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_no_method_B_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.BLOCK_HOUR)
                                                .build(),
                                        AircraftTypeDetails.builder()
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

        final EmissionsMonitoringPlanValidationResult actual = methodBProceduresSectionValidator.validate(empContainer);
        assertTrue(actual.isValid());
        assertThat(actual.getEmpViolations()).isEmpty();
    }

    @Test
    void when_method_B_no_method_B_procedures_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetails.builder()
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

        final EmissionsMonitoringPlanValidationResult actual = methodBProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_B_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpMethodBProcedures.class.getSimpleName());
    }

    @Test
    void when_no_method_B_and_method_B_procedures_exist_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.METHOD_A)
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .fuelConsumptionMeasuringMethod(FuelConsumptionMeasuringMethod.FUEL_UPLIFT)
                                                .build()))
                                .build())
                        .methodBProcedures(EmpMethodBProcedures.builder()
                                .fuelConsumptionPerFlight(createEmpProcedureForm())
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = methodBProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_B_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpMethodBProcedures.class.getSimpleName());
    }

    @Test
    void when_small_emitters_method_B_procedures_exist_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator1")
                                                        .model("model1")
                                                        .manufacturer("manufacturer1")
                                                        .build())
                                                .build(),
                                        AircraftTypeDetails.builder()
                                                .aircraftTypeInfo(AircraftTypeInfo.builder()
                                                        .designatorType("designator2")
                                                        .model("model2")
                                                        .manufacturer("manufacturer2")
                                                        .build())
                                                .build()))
                                .build())
                        .methodBProcedures(EmpMethodBProcedures.builder()
                                .fuelConsumptionPerFlight(createEmpProcedureForm())
                                .fuelDensity(createEmpProcedureForm())
                                .build())
                        .build())
                .build();

        final EmissionsMonitoringPlanValidationResult actual = methodBProceduresSectionValidator.validate(empContainer);
        assertFalse(actual.isValid());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_METHOD_B_PROCEDURES.getMessage());
        assertThat(actual.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getSectionName)
                .containsOnly(EmpMethodBProcedures.class.getSimpleName());
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

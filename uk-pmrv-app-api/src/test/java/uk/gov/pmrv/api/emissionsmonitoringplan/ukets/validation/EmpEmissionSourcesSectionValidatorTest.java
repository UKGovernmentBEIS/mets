package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class EmpEmissionSourcesSectionValidatorTest {

    @InjectMocks
    private EmpEmissionSourcesSectionValidator validator;

    @Test
    void validate_fuel_use_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertTrue(result.isValid());
        assertThat(result.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_fuel_use_no_additional_aircraft_monitoring_approach_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)))
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH.getMessage());
    }


    @Test
    void validate_fuel_use_with_missing_methods_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)
                                ))
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .contains(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD.getMessage());
    }

    @Test
    void validate_fuel_use_multiple_methods_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_B)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertTrue(result.isValid());
        assertThat(result.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_fuel_use_same_methods_with_explanation_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION.getMessage());
    }

    @Test
    void validate_fuel_use_multiple_methods_empty_explanation_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_B)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .multipleFuelConsumptionMethodsExplanation("")
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION.getMessage());
    }

    @Test
    void validate_eurocontrol_support_facility_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_A)
                                ))
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD.getMessage());
    }

    @Test
    void validate_eurocontrol_support_facility_additional_aircraft_monitoring_approach_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH.getMessage());
    }

    @Test
    void validate_small_emitters_valid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)
                                ))
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertTrue(result.isValid());
        assertThat(result.getEmpViolations()).isEmpty();
    }

    @Test
    void validate_small_emitters_with_explanation_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", null),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)))
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION.getMessage());
    }

    @Test
    void validate_fuel_use_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", null)))
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD.getMessage(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH.getMessage());
    }

    @Test
    void validate_small_emitters_invalid() {

        final EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder().monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build())
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(
                                        createAircraftTypeDetails("manufacturer 1", "model 1", "designator 1", FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer 2", "model 2", "designator 2", FuelConsumptionMeasuringMethod.METHOD_B)))
                                .additionalAircraftMonitoringApproach(createEmpProcedureForm())
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .build())
                .build();
        final EmissionsMonitoringPlanValidationResult result = validator.validate(empContainer);
        assertFalse(result.isValid());
        assertThat(result.getEmpViolations()).extracting(EmissionsMonitoringPlanViolation::getMessage)
                .containsOnly(EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MULTIPLE_FUEL_CONSUMPTION_MEASURING_METHODS_EXPLANATION.getMessage(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_FUEL_CONSUMPTION_MEASURING_METHOD.getMessage(),
                        EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_ADDITIONAL_AIRCRAFT_MONITORING_APPROACH.getMessage());
    }

    private AircraftTypeDetails createAircraftTypeDetails(String manufacturer, String model, String designator, FuelConsumptionMeasuringMethod method) {
        return AircraftTypeDetails.builder()
                .aircraftTypeInfo(AircraftTypeInfo.builder()
                        .manufacturer(manufacturer)
                        .model(model)
                        .designatorType(designator)
                        .build())
                .fuelConsumptionMeasuringMethod(method)
                .build();
    }

    private EmpProcedureForm createEmpProcedureForm() {
        return EmpProcedureForm.builder()
                .procedureDescription("description")
                .procedureReference("reference")
                .procedureDocumentName("document name")
                .locationOfRecords("records location")
                .responsibleDepartmentOrRole("responsible role")
                .itSystemUsed("IT system")
                .build();
    }
}

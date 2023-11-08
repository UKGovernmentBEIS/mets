package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmpProcedureForm;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.AircraftTypeInfo;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.FuelMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.SupportFacilityMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpDataFlowActivities;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpEnvironmentalManagementSystem;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpMonitoringReportingRoles;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpManagementProceduresSectionValidatorTest {

    private final EmpManagementProceduresSectionValidator managementProceduresSectionValidator =
            new EmpManagementProceduresSectionValidator();

    @Test
    void validate_when_support_facility_and_all_applicable_procedures_exist_then_valid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build()
                        )
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .build()
                        )
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_support_facility_and_not_all_applicable_procedures_exist_then_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                                .build()
                        )
                        .managementProcedures(EmpManagementProcedures.builder().build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());

        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());

        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of())
                        .missingMandatoryProcedures(List.of("monitoringReportingRoles", "recordKeepingAndDocumentation"))
                        .build()));
    }

    @Test
    void validate_when_support_facility_and_other_than_the_applicable_procedures_exist_then_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SUPPORT_FACILITY)
                    .build()
                )
                .managementProcedures(EmpManagementProcedures.builder()
                    .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                    .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                    .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                    .dataValidation(EmpProcedureForm.builder().build())
                    .riskAssessmentFile(UUID.randomUUID())
                    .upliftQuantityCrossChecks(EmpProcedureForm.builder().build())
                    .build()
                )
                .build())
            .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());

        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());

        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
            EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of("riskAssessmentFile", "upliftQuantityCrossChecks", "assignmentOfResponsibilities", "dataValidation"))
                        .missingMandatoryProcedures(List.of())
                        .build()));
    }

    @Test
    void validate_when_small_emitters_and_all_applicable_procedures_exist_then_valid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build()
                        )
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .build()
                        )
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_small_emitters_and_not_all_applicable_procedures_exist_then_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                                .build()
                        )
                        .managementProcedures(EmpManagementProcedures.builder().build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());

        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());

        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of())
                        .missingMandatoryProcedures(List.of("monitoringReportingRoles",
                                "recordKeepingAndDocumentation",
                                "assignmentOfResponsibilities",
                                "monitoringPlanAppropriateness",
                                "qaMeteringAndMeasuringEquipment",
                                "dataValidation",
                                "correctionsAndCorrectiveActions",
                                "controlOfOutsourcedActivities",
                                "assessAndControlRisks",
                                "dataFlowActivities",
                                "environmentalManagementSystem"))
                        .build()));

    }

    @Test
    void validate_when_small_emitters_and_other_than_the_applicable_procedures_exist_then_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
            .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                .emissionsMonitoringApproach(SupportFacilityMonitoringApproach.builder()
                    .monitoringApproachType(EmissionsMonitoringApproachType.EUROCONTROL_SMALL_EMITTERS)
                    .build()
                )
                .managementProcedures(EmpManagementProcedures.builder()
                    .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                    .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                    .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                    .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                    .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                    .dataValidation(EmpProcedureForm.builder().build())
                    .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                    .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                    .assessAndControlRisks(EmpProcedureForm.builder().build())
                    .dataFlowActivities(EmpDataFlowActivities.builder().build())
                    .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                    .riskAssessmentFile(UUID.randomUUID())
                    .upliftQuantityCrossChecks(EmpProcedureForm.builder().build())
                    .build()
                )
                .build())
            .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());

        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());

        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
            EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of( "riskAssessmentFile", "upliftQuantityCrossChecks"))
                        .missingMandatoryProcedures(new ArrayList<>())
                        .build()));
    }

    @Test
    void validate_when_fumm_then_valid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.METHOD_B)))
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .riskAssessmentFile(UUID.randomUUID())
                                .upliftQuantityCrossChecks(EmpProcedureForm.builder().build())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_fumm_no_risk_assessment_file_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.METHOD_B)))
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .upliftQuantityCrossChecks(EmpProcedureForm.builder().build())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());
        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());
        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of())
                        .missingMandatoryProcedures(List.of("riskAssessmentFile"))
                        .build()));
    }

    @Test
    void validate_when_fumm_no_uplift_quantity_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.METHOD_A),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)))
                                .multipleFuelConsumptionMethodsExplanation("explanation")
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .riskAssessmentFile(UUID.randomUUID())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());
        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());
        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of())
                        .missingMandatoryProcedures(List.of("upliftQuantityCrossChecks"))
                        .build()));
    }

    @Test
    void validate_when_fumm_no_uplift_quantity_valid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)))
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .riskAssessmentFile(UUID.randomUUID())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertTrue(validationResult.isValid());
    }

    @Test
    void validate_when_fumm_block_on_block_of_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF)))
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .monitoringReportingRoles(EmpMonitoringReportingRoles.builder().build())
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .dataFlowActivities(EmpDataFlowActivities.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .riskAssessmentFile(UUID.randomUUID())
                                .upliftQuantityCrossChecks(EmpProcedureForm.builder().build())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());
        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());
        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of("upliftQuantityCrossChecks"))
                        .missingMandatoryProcedures(List.of())
                        .build()));
    }

    @Test
    void validate_when_fumm_multiple_procedures_missing_invalid() {
        EmissionsMonitoringPlanUkEtsContainer empContainer = EmissionsMonitoringPlanUkEtsContainer.builder()
                .emissionsMonitoringPlan(EmissionsMonitoringPlanUkEts.builder()
                        .emissionsMonitoringApproach(FuelMonitoringApproach.builder()
                                .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                                .build()
                        )
                        .emissionSources(EmpEmissionSources.builder()
                                .aircraftTypes(Set.of(createAircraftTypeDetails("manufacturer1", "model1", "designator1",
                                                FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF),
                                        createAircraftTypeDetails("manufacturer2", "model2", "designator2",
                                                FuelConsumptionMeasuringMethod.FUEL_UPLIFT)))
                                .build())
                        .managementProcedures(EmpManagementProcedures.builder()
                                .recordKeepingAndDocumentation(EmpProcedureForm.builder().build())
                                .assignmentOfResponsibilities(EmpProcedureForm.builder().build())
                                .monitoringPlanAppropriateness(EmpProcedureForm.builder().build())
                                .qaMeteringAndMeasuringEquipment(EmpProcedureForm.builder().build())
                                .dataValidation(EmpProcedureForm.builder().build())
                                .correctionsAndCorrectiveActions(EmpProcedureForm.builder().build())
                                .controlOfOutsourcedActivities(EmpProcedureForm.builder().build())
                                .assessAndControlRisks(EmpProcedureForm.builder().build())
                                .environmentalManagementSystem(EmpEnvironmentalManagementSystem.builder().build())
                                .build())
                        .build())
                .build();

        EmissionsMonitoringPlanValidationResult validationResult = managementProceduresSectionValidator.validate(empContainer);

        assertFalse(validationResult.isValid());
        List<EmissionsMonitoringPlanViolation> empViolations = validationResult.getEmpViolations();
        assertEquals(1, empViolations.size());
        assertThat(empViolations).containsOnly(new EmissionsMonitoringPlanViolation(EmpManagementProcedures.class.getSimpleName(),
                EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                EmpManagementProceduresSectionValidator.ValidationResult.builder()
                        .additionalUnnecessaryProcedures(List.of())
                        .missingMandatoryProcedures(List.of("monitoringReportingRoles", "dataFlowActivities", "riskAssessmentFile", "upliftQuantityCrossChecks"))
                        .build()));
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
}
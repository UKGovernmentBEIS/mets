package uk.gov.pmrv.api.emissionsmonitoringplan.ukets.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanValidationResult;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.EmissionsMonitoringPlanViolation;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsContainer;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmpEmissionsMonitoringApproach;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.AircraftTypeDetails;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsources.EmpEmissionSources;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources.FuelConsumptionMeasuringMethod;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.managementprocedures.EmpManagementProcedures;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmpManagementProceduresSectionValidator implements EmpUkEtsContextValidator {

    @Override
    public EmissionsMonitoringPlanValidationResult validate(EmissionsMonitoringPlanUkEtsContainer empContainer) {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlan = empContainer.getEmissionsMonitoringPlan();
        EmpEmissionsMonitoringApproach emissionsMonitoringApproach = emissionsMonitoringPlan.getEmissionsMonitoringApproach();
        EmpManagementProcedures managementProcedures = emissionsMonitoringPlan.getManagementProcedures();
        EmpEmissionSources emissionSources = emissionsMonitoringPlan.getEmissionSources();

        ValidationResult validationResult =
                validateManagementProceduresExistencePerMonitoringApproachType(emissionsMonitoringApproach.getMonitoringApproachType(), managementProcedures, emissionSources);

        if (!validationResult.getMissingMandatoryProcedures().isEmpty() || !validationResult.getAdditionalUnnecessaryProcedures().isEmpty()) {
            List<EmissionsMonitoringPlanViolation> empViolations = List.of(new EmissionsMonitoringPlanViolation(
                            EmpManagementProcedures.class.getSimpleName(),
                            EmissionsMonitoringPlanViolation.ViolationMessage.INVALID_MANAGEMENT_PROCEDURES,
                            validationResult
                    )
            );

            return EmissionsMonitoringPlanValidationResult.invalidEmissionsMonitoringPlan(empViolations);
        }

        return EmissionsMonitoringPlanValidationResult.validEmissionsMonitoringPlan();
    }

    private ValidationResult validateManagementProceduresExistencePerMonitoringApproachType(EmissionsMonitoringApproachType monitoringApproachType,
                                                                                            EmpManagementProcedures managementProcedures,
                                                                                            EmpEmissionSources emissionSources) {
        return switch (monitoringApproachType) {
            case EUROCONTROL_SUPPORT_FACILITY ->
                    validateManagementProceduresForSupportFacility(managementProcedures);
            case EUROCONTROL_SMALL_EMITTERS ->
                    validateManagementProceduresForSmallEmitters(managementProcedures);
            case FUEL_USE_MONITORING ->
                    validateManagementProceduresForFUMM(managementProcedures, emissionSources);
        };
    }

    private ValidationResult validateManagementProceduresForSupportFacility(EmpManagementProcedures managementProcedures) {
        return ValidationResult.builder()
                .missingMandatoryProcedures(validateManagementProceduresExistenceForSupportFacility(managementProcedures))
                .additionalUnnecessaryProcedures(validateManagementProceduresAbsenceForSupportFacility(managementProcedures))
                .build();
    }

    private ValidationResult validateManagementProceduresForSmallEmitters(EmpManagementProcedures managementProcedures) {
        return ValidationResult.builder()
                .missingMandatoryProcedures(validateManagementProceduresExistenceForSmallEmitters(managementProcedures))
                .additionalUnnecessaryProcedures(validateManagementProceduresAbsenceForSmallEmitters(managementProcedures))
                .build();
    }

    private List<String> validateManagementProceduresExistenceForSupportFacility(EmpManagementProcedures managementProcedures) {
        List<String> missingMandatoryProcedures = new ArrayList<>();

        if (managementProcedures.getMonitoringReportingRoles() == null) {
            missingMandatoryProcedures.add("monitoringReportingRoles");
        }
        if (managementProcedures.getRecordKeepingAndDocumentation() == null) {
            missingMandatoryProcedures.add("recordKeepingAndDocumentation");
        }

        return missingMandatoryProcedures;
    }

    private ValidationResult validateManagementProceduresForFUMM(EmpManagementProcedures managementProcedures,
                                                                 EmpEmissionSources emissionSources) {

        List<String> missingMandatoryProcedures = validateManagementProceduresExistenceForSmallEmitters(managementProcedures);
        ValidationResult validationResult = ValidationResult.builder()
                .missingMandatoryProcedures(missingMandatoryProcedures)
                .additionalUnnecessaryProcedures(new ArrayList<>())
                .build();

        if (managementProcedures.getRiskAssessmentFile() == null) {
            validationResult.getMissingMandatoryProcedures().add("riskAssessmentFile");
        }

        if ((!checkIfOnlyBlockOnBlockOffMethodExists(emissionSources) && managementProcedures.getUpliftQuantityCrossChecks() == null)) {
            validationResult.getMissingMandatoryProcedures().add("upliftQuantityCrossChecks");

        }
        if (checkIfOnlyBlockOnBlockOffMethodExists(emissionSources) && managementProcedures.getUpliftQuantityCrossChecks() != null) {
            validationResult.getAdditionalUnnecessaryProcedures().add("upliftQuantityCrossChecks");
        }
        return validationResult;
    }

    private List<String> validateManagementProceduresExistenceForSmallEmitters(EmpManagementProcedures managementProcedures) {
        List<String> missingMandatoryProcedures = validateManagementProceduresExistenceForSupportFacility(managementProcedures);

        if (managementProcedures.getAssignmentOfResponsibilities() == null) {
            missingMandatoryProcedures.add("assignmentOfResponsibilities");
        }
        if (managementProcedures.getMonitoringPlanAppropriateness() == null) {
            missingMandatoryProcedures.add("monitoringPlanAppropriateness");
        }
        if (managementProcedures.getQaMeteringAndMeasuringEquipment() == null) {
            missingMandatoryProcedures.add("qaMeteringAndMeasuringEquipment");
        }
        if (managementProcedures.getDataValidation() == null) {
            missingMandatoryProcedures.add("dataValidation");
        }
        if (managementProcedures.getCorrectionsAndCorrectiveActions() == null) {
            missingMandatoryProcedures.add("correctionsAndCorrectiveActions");
        }
        if (managementProcedures.getControlOfOutsourcedActivities() == null) {
            missingMandatoryProcedures.add("controlOfOutsourcedActivities");
        }
        if (managementProcedures.getAssessAndControlRisks() == null) {
            missingMandatoryProcedures.add("assessAndControlRisks");
        }
        if (managementProcedures.getDataFlowActivities() == null) {
            missingMandatoryProcedures.add("dataFlowActivities");
        }
        if (managementProcedures.getEnvironmentalManagementSystem() == null) {
            missingMandatoryProcedures.add("environmentalManagementSystem");
        }

        return missingMandatoryProcedures;
    }

    private List<String> validateManagementProceduresAbsenceForSupportFacility(EmpManagementProcedures managementProcedures) {
        List<String> additionalUnnecessaryProcedures = validateManagementProceduresAbsenceForSmallEmitters(managementProcedures);

        if (managementProcedures.getAssignmentOfResponsibilities() != null) {
            additionalUnnecessaryProcedures.add("assignmentOfResponsibilities");
        }
        if (managementProcedures.getMonitoringPlanAppropriateness() != null) {
            additionalUnnecessaryProcedures.add("monitoringPlanAppropriateness");
        }
        if (managementProcedures.getQaMeteringAndMeasuringEquipment() != null) {
            additionalUnnecessaryProcedures.add("qaMeteringAndMeasuringEquipment");
        }
        if (managementProcedures.getDataValidation() != null) {
            additionalUnnecessaryProcedures.add("dataValidation");
        }
        if (managementProcedures.getCorrectionsAndCorrectiveActions() != null) {
            additionalUnnecessaryProcedures.add("correctionsAndCorrectiveActions");
        }
        if (managementProcedures.getControlOfOutsourcedActivities() != null) {
            additionalUnnecessaryProcedures.add("controlOfOutsourcedActivities");
        }
        if (managementProcedures.getAssessAndControlRisks() != null) {
            additionalUnnecessaryProcedures.add("assessAndControlRisks");
        }
        if (managementProcedures.getDataFlowActivities() != null) {
            additionalUnnecessaryProcedures.add("dataFlowActivities");
        }
        if (managementProcedures.getEnvironmentalManagementSystem() != null) {
            additionalUnnecessaryProcedures.add("environmentalManagementSystem");
        }

        return additionalUnnecessaryProcedures;
    }

    private List<String> validateManagementProceduresAbsenceForSmallEmitters(EmpManagementProcedures managementProcedures) {
        List<String> additionalUnnecessaryProcedures = new ArrayList<>();

        if (managementProcedures.getRiskAssessmentFile() != null) {
            additionalUnnecessaryProcedures.add("riskAssessmentFile");
        }
        if (managementProcedures.getUpliftQuantityCrossChecks() != null) {
            additionalUnnecessaryProcedures.add("upliftQuantityCrossChecks");
        }

        return additionalUnnecessaryProcedures;
    }

    private boolean checkIfOnlyBlockOnBlockOffMethodExists(EmpEmissionSources emissionSources) {
        return emissionSources.getAircraftTypes().stream()
                .map(AircraftTypeDetails::getFuelConsumptionMeasuringMethod)
                .allMatch(FuelConsumptionMeasuringMethod.BLOCK_ON_BLOCK_OFF::equals);
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class ValidationResult {
        private List<String> missingMandatoryProcedures;
        private List<String> additionalUnnecessaryProcedures;
    }
}

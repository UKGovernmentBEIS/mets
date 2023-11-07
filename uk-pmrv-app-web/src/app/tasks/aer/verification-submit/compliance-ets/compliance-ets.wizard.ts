import { EtsComplianceRules } from 'pmrv-api';

export function complianceEtsWizard(complianceRulesInfo: EtsComplianceRules): boolean {
  return (
    !!complianceRulesInfo &&
    complianceRulesInfo.monitoringPlanRequirementsMet !== undefined &&
    complianceRulesInfo.euRegulationMonitoringReportingMet !== undefined &&
    complianceRulesInfo.detailSourceDataVerified !== undefined &&
    complianceRulesInfo.controlActivitiesDocumented !== undefined &&
    complianceRulesInfo.proceduresMonitoringPlanDocumented !== undefined &&
    complianceRulesInfo.dataVerificationCompleted !== undefined &&
    complianceRulesInfo.monitoringApproachAppliedCorrectly !== undefined &&
    complianceRulesInfo.plannedActualChangesReported !== undefined &&
    complianceRulesInfo.methodsApplyingMissingDataUsed !== undefined &&
    complianceRulesInfo.uncertaintyAssessment !== undefined &&
    complianceRulesInfo.competentAuthorityGuidanceMet !== undefined &&
    !!complianceRulesInfo.nonConformities
  );
}

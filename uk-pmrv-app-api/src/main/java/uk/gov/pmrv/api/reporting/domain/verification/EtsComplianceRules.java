package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#monitoringPlanRequirementsMet) != (#monitoringPlanRequirementsNotMetReason != null)}", message = "aerVerificationData.etsComplianceRules.monitoringPlanRequirements")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#euRegulationMonitoringReportingMet) != (#euRegulationMonitoringReportingNotMetReason != null)}", message = "aerVerificationData.etsComplianceRules.euRegulationMonitoringReporting")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#detailSourceDataVerified) != (#detailSourceDataNotVerifiedReason != null)}", message = "aerVerificationData.etsComplianceRules.verifyDetailSourceData.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#detailSourceDataVerified) == (#partOfSiteVerification != null)}", message = "aerVerificationData.etsComplianceRules.verifyDetailSourceData.partOfSiteVerification")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#controlActivitiesDocumented) != (#controlActivitiesNotDocumentedReason != null)}", message = "aerVerificationData.etsComplianceRules.controlActivities")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#proceduresMonitoringPlanDocumented) != (#proceduresMonitoringPlanNotDocumentedReason != null)}", message = "aerVerificationData.etsComplianceRules.proceduresMonitoringPlan")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#dataVerificationCompleted) != (#dataVerificationNotCompletedReason != null)}", message = "aerVerificationData.etsComplianceRules.dataVerification")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#monitoringApproachAppliedCorrectly) != (#monitoringApproachNotAppliedCorrectlyReason != null)}", message = "aerVerificationData.etsComplianceRules.monitoringApproachAppliedCorrectly")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#plannedActualChangesReported) != (#plannedActualChangesNotReportedReason != null)}", message = "aerVerificationData.etsComplianceRules.reportPlannedActualChanges")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#methodsApplyingMissingDataUsed) != (#methodsApplyingMissingDataNotUsedReason != null)}", message = "aerVerificationData.etsComplianceRules.methodsApplyingMissingData")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#uncertaintyAssessment) != (#uncertaintyAssessmentNotUsedReason != null)}", message = "aerVerificationData.etsComplianceRules.uncertaintyAssessment")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#competentAuthorityGuidanceMet) != (#competentAuthorityGuidanceNotMetReason != null)}", message = "aerVerificationData.etsComplianceRules.competentAuthorityGuidance")

public class EtsComplianceRules {

    @NotNull
    private Boolean monitoringPlanRequirementsMet;
    private String monitoringPlanRequirementsNotMetReason;

    @NotNull
    private Boolean euRegulationMonitoringReportingMet;
    private String euRegulationMonitoringReportingNotMetReason;

    @NotNull
    private Boolean detailSourceDataVerified;
    private String detailSourceDataNotVerifiedReason;
    private String partOfSiteVerification;

    @NotNull
    private Boolean controlActivitiesDocumented;
    private String controlActivitiesNotDocumentedReason;

    @NotNull
    private Boolean proceduresMonitoringPlanDocumented;
    private String proceduresMonitoringPlanNotDocumentedReason;

    @NotNull
    private Boolean dataVerificationCompleted;
    private String dataVerificationNotCompletedReason;

    @NotNull
    private Boolean monitoringApproachAppliedCorrectly;
    private String monitoringApproachNotAppliedCorrectlyReason;

    @NotNull
    private Boolean plannedActualChangesReported;
    private String plannedActualChangesNotReportedReason;

    @NotNull
    private Boolean methodsApplyingMissingDataUsed;
    private String methodsApplyingMissingDataNotUsedReason;

    @NotNull
    private Boolean uncertaintyAssessment;
    private String uncertaintyAssessmentNotUsedReason;

    @NotNull
    private Boolean competentAuthorityGuidanceMet;
    private String competentAuthorityGuidanceNotMetReason;

    @NotNull
    private NonConformities nonConformities;

}

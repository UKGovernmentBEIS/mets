package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#monitoringPlanRequirementsMet) == (#monitoringPlanRequirementsNotMetReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.monitoringPlanRequirementsMet.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#ukEtsOrderRequirementsMet) == (#ukEtsOrderRequirementsNotMetReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.ukEtsOrderRequirementsMet.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#detailSourceDataVerified) == (#detailSourceDataNotVerifiedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.detailSourceDataVerified.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#detailSourceDataVerified) == (#partOfSiteVerification != null)}", message = "aviationAerVerificationData.etsComplianceRules.detailSourceDataVerified.partOfSiteVerification")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#controlActivitiesDocumented) == (#controlActivitiesNotDocumentedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.controlActivitiesDocumented.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#proceduresMonitoringPlanDocumented) == (#proceduresMonitoringPlanNotDocumentedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.proceduresMonitoringPlanDocumented.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#dataVerificationCompleted) == (#dataVerificationNotCompletedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.dataVerificationCompleted.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#monitoringApproachAppliedCorrectly) == (#monitoringApproachNotAppliedCorrectlyReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.monitoringApproachAppliedCorrectly.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#flightsCompletenessComparedWithAirTrafficData) == (#flightsCompletenessNotComparedWithAirTrafficDataReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.flightsCompletenessComparedWithAirTrafficData.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#reportedDataConsistencyChecksPerformed) == (#reportedDataConsistencyChecksNotPerformedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.reportedDataConsistencyChecksPerformed.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#fuelConsistencyChecksPerformed) == (#fuelConsistencyChecksNotPerformedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.fuelConsistencyChecksPerformed.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#missingDataMethodsApplied) == (#missingDataMethodsNotAppliedReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.missingDataMethodsApplied.reason")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#regulatorGuidanceMet) == (#regulatorGuidanceNotMetReason != null)}", message = "aviationAerVerificationData.etsComplianceRules.regulatorGuidanceMet.reason")
public class AviationAerEtsComplianceRules {

    //Have the emissions monitoring plan requirements and conditions been met?
    @NotNull
    private Boolean monitoringPlanRequirementsMet;
    @Size(max = 10000)
    private String monitoringPlanRequirementsNotMetReason;

    //Have the requirements of the UK ETS Order, which amends the MRR, been met?
    @NotNull
    private Boolean ukEtsOrderRequirementsMet;
    @Size(max = 10000)
    private String ukEtsOrderRequirementsNotMetReason;

    //Can you verify the detail and source of data?
    @NotNull
    private Boolean detailSourceDataVerified;
    @Size(max = 10000)
    private String detailSourceDataNotVerifiedReason;
    @Size(max = 10000)
    private String partOfSiteVerification;

    //Were control activities documented, implemented, maintained and effective to reduce any risks?
    @NotNull
    private Boolean controlActivitiesDocumented;
    @Size(max = 10000)
    private String controlActivitiesNotDocumentedReason;

    //Were procedures in the emissions monitoring plan documented, implemented, maintained and effective to reduce risks?
    @NotNull
    private Boolean proceduresMonitoringPlanDocumented;
    @Size(max = 10000)
    private String proceduresMonitoringPlanNotDocumentedReason;

    //Has data verification been completed as required?
    @NotNull
    private Boolean dataVerificationCompleted;
    @Size(max = 10000)
    private String dataVerificationNotCompletedReason;

    //Has the monitoring approach been applied correctly?
    @NotNull
    private Boolean monitoringApproachAppliedCorrectly;
    @Size(max = 10000)
    private String monitoringApproachNotAppliedCorrectlyReason;

    //Has the completeness of flights or data been compared with air traffic data?
    @NotNull
    private Boolean flightsCompletenessComparedWithAirTrafficData;
    @Size(max = 10000)
    private String flightsCompletenessNotComparedWithAirTrafficDataReason;

    //Have checks for consistency between reported data and 'mass & balance' documentation been performed?
    @NotNull
    private Boolean reportedDataConsistencyChecksPerformed;
    @Size(max = 10000)
    private String reportedDataConsistencyChecksNotPerformedReason;

    //Have checks for consistency between aggregate fuel consumption and fuel purchase or supply data been performed?
    @NotNull
    private Boolean fuelConsistencyChecksPerformed;
    @Size(max = 10000)
    private String fuelConsistencyChecksNotPerformedReason;

    //Were methods used for applying missing data appropriate?
    @NotNull
    private Boolean missingDataMethodsApplied;
    @Size(max = 10000)
    private String missingDataMethodsNotAppliedReason;

    //Has the regulator guidance on monitoring and reporting been met?
    @NotNull
    private Boolean regulatorGuidanceMet;
    @Size(max = 10000)
    private String regulatorGuidanceNotMetReason;

    //Have any non-conformities from last year been corrected?
    @NotNull
    private AviationAerNonConformities nonConformities;
}

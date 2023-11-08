package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.reporting.domain.ActivityLevelReport;

@Data
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AerVerificationData {

    @NotNull
    @Valid
    private VerificationTeamDetails verificationTeamDetails;

    @NotNull
    @Valid
    private VerifierContact verifierContact;

    @NotNull
    @Valid
    private MaterialityLevel materialityLevel;

    @NotNull
    @Valid
    private ComplianceMonitoringReporting complianceMonitoringReporting;

    @NotNull
    @Valid
    private EtsComplianceRules etsComplianceRules;

    @NotNull
    @Valid
    private SummaryOfConditions summaryOfConditions;

    @NotNull
    @Valid
    private OverallAssessment overallAssessment;

    @NotNull
    @Valid
    private MethodologiesToCloseDataGaps methodologiesToCloseDataGaps;

    @NotNull
    @Valid
    private OpinionStatement opinionStatement;

    @NotNull
    @Valid
    private UncorrectedMisstatements uncorrectedMisstatements;

    @NotNull
    @Valid
    private RecommendedImprovements recommendedImprovements;

    @NotNull
    @Valid
    private UncorrectedNonConformities uncorrectedNonConformities;

    @NotNull
    @Valid
    private UncorrectedNonCompliances uncorrectedNonCompliances;

    @Valid
    private ActivityLevelReport activityLevelReport;
}

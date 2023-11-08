package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerVerificationData;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerRecommendedImprovements;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerUncorrectedNonCompliances;
import uk.gov.pmrv.api.aviationreporting.common.domain.verification.AviationAerVerificationDecision;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerUncorrectedMisstatements;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerUkEtsVerificationData implements AviationAerVerificationData {

    @NotNull
    @Valid
    private AviationAerVerifierContact verifierContact;

    @NotNull
    @Valid
    private AviationAerVerificationTeamDetails verificationTeamDetails;

    @NotNull
    @Valid
    private AviationAerVerificationDecision overallDecision;

    @NotNull
    @Valid
    private AviationAerEtsComplianceRules etsComplianceRules;

    @NotNull
    @Valid
    private AviationAerComplianceMonitoringReportingRules complianceMonitoringReportingRules;

    @NotNull
    @Valid
    private AviationAerOpinionStatement opinionStatement;

    @NotNull
    @Valid
    private AviationAerUncorrectedMisstatements uncorrectedMisstatements;

    @NotNull
    @Valid
    private AviationAerUncorrectedNonCompliances uncorrectedNonCompliances;

    @NotNull
    @Valid
    private AviationAerUncorrectedNonConformities uncorrectedNonConformities;

    @NotNull
    @Valid
    private AviationAerRecommendedImprovements recommendedImprovements;

    @Valid
    private AviationAerEmissionsReductionClaimVerification emissionsReductionClaimVerification;

    @NotNull
    @Valid
    private AviationAerDataGapsMethodologies dataGapsMethodologies;

    @NotNull
    @Valid
    private AviationAerMaterialityLevel materialityLevel;

    @Override
    @JsonIgnore
    public boolean isValidForVir() {

        if (uncorrectedNonConformities == null || recommendedImprovements == null) {
            return false;
        }
        return Boolean.TRUE.equals(uncorrectedNonConformities.getExistUncorrectedNonConformities()) ||
               Boolean.TRUE.equals(uncorrectedNonConformities.getExistPriorYearIssues()) ||
               Boolean.TRUE.equals(recommendedImprovements.getExist());
    }
}

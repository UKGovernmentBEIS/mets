package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

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
public class AviationAerCorsiaVerificationData implements AviationAerVerificationData {

    @NotNull
    @Valid
    private AviationAerCorsiaVerifierDetails verifierDetails;

    @Valid
    private AviationAerCorsiaEmissionsReductionClaimVerification emissionsReductionClaimVerification;

    @NotNull
    @Valid
    private AviationAerCorsiaIndependentReview independentReview;

    @NotNull
    @Valid
    private AviationAerCorsiaVerifiersConclusions verifiersConclusions;

    @NotNull
    @Valid
    private AviationAerCorsiaTimeAllocationScope timeAllocationScope;

    @NotNull
    @Valid
    private AviationAerCorsiaProcessAnalysis processAnalysis;

    @NotNull
    @Valid
    private AviationAerCorsiaGeneralInformation generalInformation;

    @NotNull
    @Valid
    private AviationAerVerificationDecision overallDecision;

    @NotNull
    @Valid
    private AviationAerCorsiaOpinionStatement opinionStatement;

    @NotNull
    @Valid
    private AviationAerUncorrectedMisstatements uncorrectedMisstatements;

    @NotNull
    @Valid
    private AviationAerRecommendedImprovements recommendedImprovements;

    @NotNull
    @Valid
    private AviationAerCorsiaUncorrectedNonConformities uncorrectedNonConformities;

    @NotNull
    @Valid
    private AviationAerUncorrectedNonCompliances uncorrectedNonCompliances;
    
    @Override
    @JsonIgnore
    public boolean isValidForVir() {
        
        if (uncorrectedNonConformities == null || recommendedImprovements == null) {
            return false;
        }
        return Boolean.TRUE.equals(uncorrectedNonConformities.getExistUncorrectedNonConformities()) ||
               Boolean.TRUE.equals(recommendedImprovements.getExist());
    }
}

package uk.gov.pmrv.api.aviationreporting.ukets.domain.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#safBatchClaimsReviewed) == (#batchReferencesNotReviewed != null)}", message = "aviationAerVerificationData.emissionsReductionClaimVerification.safBatchClaimsReviewed.referencesNotReviewed")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#safBatchClaimsReviewed) == (#dataSampling != null)}", message = "aviationAerVerificationData.emissionsReductionClaimVerification.safBatchClaimsReviewed.dataSampling")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#evidenceAllCriteriaMetExist) == (#noCriteriaMetIssues != null)}", message = "aviationAerVerificationData.emissionsReductionClaimVerification.evidenceAllCriteriaMetExist.issues")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#complianceWithUkEtsRequirementsExist) == (#notCompliedWithUkEtsRequirementsAspects != null)}", message = "aviationAerVerificationData.emissionsReductionClaimVerification.complianceWithUkEtsRequirementsExist.aspects")
public class AviationAerEmissionsReductionClaimVerification {

    @NotNull
    private Boolean safBatchClaimsReviewed;

    @Size(max = 10000)
    private String batchReferencesNotReviewed;

    @Size(max = 10000)
    private String dataSampling;

    @NotBlank
    @Size(max = 10000)
    private String reviewResults;

    @NotBlank
    @Size(max = 10000)
    private String noDoubleCountingConfirmation;

    @NotNull
    private Boolean evidenceAllCriteriaMetExist;

    @Size(max = 10000)
    private String noCriteriaMetIssues;

    @NotNull
    private Boolean complianceWithUkEtsRequirementsExist;

    @Size(max = 10000)
    private String notCompliedWithUkEtsRequirementsAspects;
}

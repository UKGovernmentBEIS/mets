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
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#methodRequired) == (#methodApproved != null)}", message = "aviationAerVerificationData.dataGapsMethodologies.methodRequired.methodApproved")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#methodApproved) == (#methodConservative != null)}", message = "aviationAerVerificationData.dataGapsMethodologies.methodApproved.methodConservative")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#methodApproved) == (#materialMisstatementExist != null)}", message = "aviationAerVerificationData.dataGapsMethodologies.methodApproved.materialMisstatementExist")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#methodConservative) == (#noConservativeMethodDetails != null)}", message = "aviationAerVerificationData.dataGapsMethodologies.methodConservative.details")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#materialMisstatementExist) == (#materialMisstatementDetails != null)}", message = "aviationAerVerificationData.dataGapsMethodologies.materialMisstatementExist.details")
public class AviationAerDataGapsMethodologies {

    @NotNull
    private Boolean methodRequired;

    private Boolean methodApproved;

    private Boolean methodConservative;

    @Size(max = 10000)
    private String noConservativeMethodDetails;

    private Boolean materialMisstatementExist;

    @Size(max = 10000)
    private String materialMisstatementDetails;
}

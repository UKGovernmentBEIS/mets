package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueField;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{(#type eq 'UNCORRECTED_MATERIAL_MISSTATEMENT' || #type eq 'UNCORRECTED_MATERIAL_NON_CONFORMITY') == (#details == null)}",
    message = "aviationAerVerificationData.decision.notVerifiedDecisionReason.details")
public class AviationAerNotVerifiedDecisionReason {

    @UniqueField
    @NotNull
    private AviationAerNotVerifiedDecisionReasonType type;

    @Size(max = 10000)
    private String details;
}

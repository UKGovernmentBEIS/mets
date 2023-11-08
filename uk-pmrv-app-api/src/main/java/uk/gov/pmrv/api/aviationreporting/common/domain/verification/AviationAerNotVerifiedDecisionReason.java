package uk.gov.pmrv.api.aviationreporting.common.domain.verification;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@SpELExpression(expression = "{(#type eq 'UNCORRECTED_MATERIAL_MISSTATEMENT' || #type eq 'UNCORRECTED_MATERIAL_NON_CONFORMITY') == (#details == null)}",
    message = "aviationAerVerificationData.decision.notVerifiedDecisionReason.details")
public class AviationAerNotVerifiedDecisionReason {

    @EqualsAndHashCode.Include
    @NotNull
    private AviationAerNotVerifiedDecisionReasonType type;

    @Size(max = 10000)
    private String details;
}

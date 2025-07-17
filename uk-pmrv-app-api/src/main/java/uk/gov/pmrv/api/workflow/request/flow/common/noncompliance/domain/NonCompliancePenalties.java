package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(
    expression = "{T(java.lang.Boolean).FALSE.equals(#civilPenalty) == (#noCivilPenaltyJustification != null)}",
    message = "non.compliance.no.civil.penalty.justification"
)
@SpELExpression(
    expression = "{T(java.lang.Boolean).TRUE.equals(#civilPenalty) == (#noticeOfIntent != null)}",
    message = "non.compliance.no.civil.penalty.noticeOfIntent"
)
@SpELExpression(
    expression = "{T(java.lang.Boolean).TRUE.equals(#civilPenalty) == (#dailyPenalty != null)}",
    message = "non.compliance.no.civil.penalty.dailyPenalty"
)
public class NonCompliancePenalties {

    @NotNull
    private Boolean civilPenalty;

    @Size(max = 10000)
    private String noCivilPenaltyJustification;

    private Boolean noticeOfIntent;

    private Boolean dailyPenalty;
}

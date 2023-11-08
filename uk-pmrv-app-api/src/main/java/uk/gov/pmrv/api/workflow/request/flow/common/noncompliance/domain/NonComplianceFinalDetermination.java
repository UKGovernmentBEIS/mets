package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(
    expression = "{T(java.lang.Boolean).TRUE.equals(#complianceRestored) == (#complianceRestoredDate != null)}",
    message = "non.compliance.restoration.date"
)
@SpELExpression(
    expression = "{T(java.lang.Boolean).TRUE.equals(#operatorPaid) == (#operatorPaidDate != null)}",
    message = "non.compliance.payment.date"
)
public class NonComplianceFinalDetermination {

    @NotNull
    private Boolean complianceRestored;

    @PastOrPresent
    private LocalDate complianceRestoredDate;

    @NotNull
    @Size(max = 10000)
    private String comments;

    @NotNull
    private Boolean reissuePenalty;

    private Boolean operatorPaid;

    @PastOrPresent
    private LocalDate operatorPaidDate;
}

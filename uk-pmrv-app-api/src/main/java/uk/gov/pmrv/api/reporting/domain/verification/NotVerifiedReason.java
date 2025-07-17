package uk.gov.pmrv.api.reporting.domain.verification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(expression = "{(#type eq 'ANOTHER_REASON') == (#otherReason != null)}", message = "aerVerificationData.overallAssessment.otherNotOverallVerificationReason")
public class NotVerifiedReason {

    @NotNull
    private NotOverallVerificationReasonType type;

    @Size(max = 10000)
    private String otherReason;
}

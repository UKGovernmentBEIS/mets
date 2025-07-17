package uk.gov.pmrv.api.workflow.request.flow.rde.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#decision eq 'REJECTED') == (#reason != null)}", message = "rde.incompatible.decision.reason")
public class RdeDecisionPayload {

    @NotNull
    private RdeDecisionType decision;

    @Size(max = 10000)
    private String reason;
}

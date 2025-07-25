package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.cessation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#allowancesSurrenderRequired == (#cessation.allowancesSurrenderDate != null)}",
    message = "permit.cessation.allowancesSurrenderDate")
@SpELExpression(expression = "{#allowancesSurrenderRequired == (#cessation.numberOfSurrenderAllowances != null)}",
    message = "permit.cessation.numberOfSurrenderAllowances")
public class PermitCessationContainer {

    private boolean allowancesSurrenderRequired;

    @Valid
    @NotNull
    private PermitCessation cessation;
}

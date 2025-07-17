package uk.gov.pmrv.api.permit.domain.estimatedannualemissions;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.PermitSection;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#quantity?.scale() < 2)}", message = "permit.estimatedAnnualEmissions.quantity")
public class EstimatedAnnualEmissions implements PermitSection {

    @NotNull
    @Positive
    private BigDecimal quantity;
}

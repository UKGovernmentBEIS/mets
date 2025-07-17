package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(#type eq 'APPROVED') || (#type eq 'REJECTED' && #reason != null) || (#type eq 'DEEMED_WITHDRAWN' && #reason != null)}", message = "emp.determination.reason")
public class EmpVariationDetermination {
	
	@NotNull
    private EmpVariationDeterminationType type;

    @Size(max = 10000)
    private String reason;
}

package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@SpELExpression(expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.PermitVariationModificationType).valueOf(#type).otherSummaryApplies) == (#otherSummary != null)}", 
	message = "permitvariation.modification.typeOtherSummary")
public class PermitVariationModification {
	
	@NotNull
	private PermitVariationModificationType type;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String otherSummary;
	
}

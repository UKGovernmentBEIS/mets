package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain.DreDeterminationReasonType).valueOf(#type).otherReasonApplies) == (#typeOtherSummary != null)}", 
message = "dre.determinationreason.type.typeOtherSummary")
public class DreDeterminationReason {

	@NotNull
	private DreDeterminationReasonType type;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String typeOtherSummary;
	
	private boolean operatorAskedToResubmit;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String regulatorComments;
	
	@Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingDocuments = new HashSet<>();
}

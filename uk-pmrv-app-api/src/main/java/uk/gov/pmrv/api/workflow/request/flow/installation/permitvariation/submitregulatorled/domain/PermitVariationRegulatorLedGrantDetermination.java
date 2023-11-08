package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.submitregulatorled.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.GrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.common.domain.review.PermitVariationReasonTemplate).valueOf(#reasonTemplate).otherSummaryApplies) == (#reasonTemplateOtherSummary != null)}", 
	message = "permitvariation.regulatorled.grantdetermination.reasontemplate.typeOtherSummary")
@JsonIgnoreProperties(value = { "type" })
public class PermitVariationRegulatorLedGrantDetermination extends GrantDetermination {
	
	@Size(max = 10000)
	@NotBlank
    private String logChanges;

	@NotNull
	private PermitVariationReasonTemplate reasonTemplate;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String reasonTemplateOtherSummary;
}

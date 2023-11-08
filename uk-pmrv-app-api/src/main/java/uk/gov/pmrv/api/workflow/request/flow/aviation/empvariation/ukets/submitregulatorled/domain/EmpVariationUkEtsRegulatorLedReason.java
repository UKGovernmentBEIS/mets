package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(T(uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.submitregulatorled.domain.EmpVariationUkEtsRegulatorLedReasonType).valueOf(#type).otherSummaryApplies) == (#reasonOtherSummary != null)}", 
	message = "empvariation.ukets.submitregulatorled.reason.typeOtherSummary")
public class EmpVariationUkEtsRegulatorLedReason {
	
	@NotNull
	private EmpVariationUkEtsRegulatorLedReasonType type;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Size(max=10000)
	private String reasonOtherSummary;
}

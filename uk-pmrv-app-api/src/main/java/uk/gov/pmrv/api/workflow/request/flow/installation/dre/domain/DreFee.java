package uk.gov.pmrv.api.workflow.request.flow.installation.dre.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#chargeOperator == (#feeDetails != null)}", message = "dre.chargeoperatorfeedetails")
public class DreFee {
	
	private boolean chargeOperator;
	
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Valid
	private DreFeeDetails feeDetails;
	
	@JsonIgnore
	public BigDecimal getFeeAmount() {
		return chargeOperator ? feeDetails.getTotalBillableHours().multiply(feeDetails.getHourlyRate()).setScale(2,
				RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}
}

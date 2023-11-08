package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{#chargeOperator == (#feeDetails != null)}", message = "aviationdDre.fee.chargeOperator.feeDetails")
public class AviationDreFee {

    private boolean chargeOperator;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private AviationDreFeeDetails feeDetails;

    @JsonIgnore
    public BigDecimal getFeeAmount() {
        return chargeOperator ? feeDetails.getTotalBillableHours().multiply(feeDetails.getHourlyRate()).setScale(2,
            RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDoECorsiaFee {

    private boolean chargeOperator;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private AviationDoECorsiaFeeDetails feeDetails;

    @JsonIgnore
    public BigDecimal getFeeAmount() {
        return chargeOperator ? feeDetails.getTotalBillableHours().multiply(feeDetails.getHourlyRate()).setScale(2,
            RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationDoECorsiaEmissions {

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal emissionsAllInternationalFlights;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal emissionsFlightsWithOffsettingRequirements;

    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal emissionsClaimFromCorsiaEligibleFuels;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max=10000)
    private String calculationApproach;
}

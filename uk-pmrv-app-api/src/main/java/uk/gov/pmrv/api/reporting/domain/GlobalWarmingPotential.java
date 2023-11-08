package uk.gov.pmrv.api.reporting.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalWarmingPotential {
    CO2(BigDecimal.ONE),
    N2O(BigDecimal.valueOf(298)),
    PFC_CF4(BigDecimal.valueOf(7390)),
    PFC_C2F6(BigDecimal.valueOf(12200));

    private final BigDecimal value;
}

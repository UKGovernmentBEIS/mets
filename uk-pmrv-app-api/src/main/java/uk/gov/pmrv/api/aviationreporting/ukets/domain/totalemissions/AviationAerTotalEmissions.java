package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAerTotalEmissions {

    @NotNull
    @Positive
    private Integer numFlightsCoveredByUkEts;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal standardFuelEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal reductionClaimEmissions;

    @NotNull
    @PositiveOrZero
    private BigDecimal totalEmissions;
}

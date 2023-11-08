package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaTotalEmissions {

    @NotNull
    @Positive
    private Integer allFlightsNumber;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal allFlightsEmissions;

    @NotNull
    @PositiveOrZero
    private Integer offsetFlightsNumber;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal offsetFlightsEmissions;

    @NotNull
    @PositiveOrZero
    private Integer nonOffsetFlightsNumber;

    @NotNull
    @PositiveOrZero
    @Digits(integer = Integer.MAX_VALUE, fraction = 0)
    private BigDecimal nonOffsetFlightsEmissions;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal emissionsReductionClaim;
}

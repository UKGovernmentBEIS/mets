package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerFlightsEmissionsDetails {

    @NotNull
    @Positive
    private Integer flightsNumber;

    @NotNull
    private AviationAerUkEtsFuelType fuelType;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal fuelConsumption;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal emissions;
}

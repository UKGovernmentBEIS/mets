package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaFlightsEmissionsDetails {

    @NotNull
    @Positive
    private Integer flightsNumber;

    @NotNull
    private AviationAerCorsiaFuelType fuelType;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal fuelConsumption;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @PositiveOrZero
    private BigDecimal emissions;

    private boolean offset;
}

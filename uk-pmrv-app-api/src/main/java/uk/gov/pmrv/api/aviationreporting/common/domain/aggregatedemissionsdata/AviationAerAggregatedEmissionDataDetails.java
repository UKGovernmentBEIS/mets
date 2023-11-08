package uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;

import java.math.BigDecimal;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AviationAerAggregatedEmissionDataDetails {

    @NotNull
    @Valid
    @EqualsAndHashCode.Include()
    private AviationRptAirportsDTO airportFrom;

    @NotNull
    @Valid
    @EqualsAndHashCode.Include()
    private AviationRptAirportsDTO airportTo;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal fuelConsumption;

    @NotNull
    @Positive
    private Integer flightsNumber;

}

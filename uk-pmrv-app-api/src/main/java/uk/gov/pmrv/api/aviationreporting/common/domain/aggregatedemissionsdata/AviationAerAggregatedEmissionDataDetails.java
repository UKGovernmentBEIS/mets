package uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueField;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;




@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public abstract class AviationAerAggregatedEmissionDataDetails {

    @NotNull
    @Valid
    @UniqueField
    private AviationRptAirportsDTO airportFrom;

    @NotNull
    @Valid
    @UniqueField
    private AviationRptAirportsDTO airportTo;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal fuelConsumption;

    @NotNull
    @Positive
    private Integer flightsNumber;

}

package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationRptAirportsDTO;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerCorsiaAerodromePairsTotalEmissions {

    @NotNull
    @Valid
    private AviationRptAirportsDTO departureAirport;

    @NotNull
    @Valid
    private AviationRptAirportsDTO arrivalAirport;

    @NotNull
    @Positive
    private Integer flightsNumber;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    private BigDecimal emissions;

    @NotNull
    private Boolean offset;
}

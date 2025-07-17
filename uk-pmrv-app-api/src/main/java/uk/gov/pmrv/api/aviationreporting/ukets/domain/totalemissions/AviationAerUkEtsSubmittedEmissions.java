package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerSubmittedEmissions;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerUkEtsSubmittedEmissions implements AviationAerSubmittedEmissions {

    @NotNull
    @Valid
    private AviationAerTotalEmissions aviationAerTotalEmissions;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid StandardFuelsTotalEmissions> standardFuelsTotalEmissions = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AerodromePairsTotalEmissions> aerodromePairsTotalEmissions = new ArrayList<>();

    @Valid
    private AviationAerDomesticFlightsEmissions domesticFlightsEmissions;

    @Valid
    private AviationAerNonDomesticFlightsEmissions nonDomesticFlightsEmissions;
}

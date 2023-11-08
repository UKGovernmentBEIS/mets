package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

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
public class AviationAerCorsiaSubmittedEmissions extends AviationAerSubmittedEmissions {

    @Valid
    private AviationAerCorsiaTotalEmissions totalEmissions;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerCorsiaAerodromePairsTotalEmissions> aerodromePairsTotalEmissions = new ArrayList<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<@NotNull @Valid AviationAerCorsiaStandardFuelsTotalEmissions> standardFuelsTotalEmissions = new ArrayList<>();

    @Valid
    private AviationAerCorsiaInternationalFlightsEmissions flightsEmissions;
}

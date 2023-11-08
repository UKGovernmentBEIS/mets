package uk.gov.pmrv.api.aviationreporting.ukets.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsAggregatedEmissionsData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.saf.AviationAerSaf;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AviationAerEmissionsCalculationDTO {

    @Valid
    @NotNull
    private AviationAerUkEtsAggregatedEmissionsData aggregatedEmissionsData;

    @Valid
    @NotNull
    private AviationAerSaf saf;
}

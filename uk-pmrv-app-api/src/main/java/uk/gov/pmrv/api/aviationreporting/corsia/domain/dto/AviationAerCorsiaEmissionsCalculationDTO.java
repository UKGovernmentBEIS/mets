package uk.gov.pmrv.api.aviationreporting.corsia.domain.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaAggregatedEmissionsData;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaEmissionsCalculationDTO {

    @Valid
    @NotNull
    protected AviationAerCorsiaAggregatedEmissionsData aggregatedEmissionsData;

    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    @Positive
    protected BigDecimal emissionsReductionClaim;

}

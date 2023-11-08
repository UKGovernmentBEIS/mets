package uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerCorsiaAggregatedEmissionDataDetails extends AviationAerAggregatedEmissionDataDetails {

    @NotNull
    @EqualsAndHashCode.Include()
    private AviationAerCorsiaFuelType fuelType;
}

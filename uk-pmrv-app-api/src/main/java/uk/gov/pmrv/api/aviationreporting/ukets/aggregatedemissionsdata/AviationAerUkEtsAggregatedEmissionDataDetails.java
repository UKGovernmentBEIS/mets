package uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.uniqueelements.UniqueField;
import uk.gov.pmrv.api.aviationreporting.common.domain.aggregatedemissionsdata.AviationAerAggregatedEmissionDataDetails;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AviationAerUkEtsAggregatedEmissionDataDetails extends AviationAerAggregatedEmissionDataDetails {

    @NotNull
    @UniqueField
    private AviationAerUkEtsFuelType fuelType;
}

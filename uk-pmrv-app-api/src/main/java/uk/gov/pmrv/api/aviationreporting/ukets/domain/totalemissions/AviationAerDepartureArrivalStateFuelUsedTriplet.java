package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;

@Data
@EqualsAndHashCode
@Builder
public class AviationAerDepartureArrivalStateFuelUsedTriplet {

    private String departureCountry;
    private String arrivalCountry;
    private AviationAerUkEtsFuelType fuelType;
}

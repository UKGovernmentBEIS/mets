package uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions;

import lombok.Builder;
import lombok.Data;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.aggregatedemissionsdata.AviationAerCorsiaFuelType;

@Data
@Builder
public class AviationAerCorsiaDepartureArrivalStateFuelUsedTriplet {

    private String departureState;
    private String arrivalState;
    private AviationAerCorsiaFuelType fuelType;
}

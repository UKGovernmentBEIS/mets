package uk.gov.pmrv.api.aviationreporting.corsia.domain.emissionsmonitoringapproach;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AviationAerCorsiaFlightType {

    ALL_INTERNATIONAL_FLIGHTS("All international flights"),
    INTERNATIONAL_FLIGHTS_WITH_NO_OFFSETTING_OBLIGATIONS("Only for international flights with no offsetting obligations"),
    INTERNATIONAL_FLIGHTS_WITH_OFFSETTING_OBLIGATIONS("Only for international flights with offsetting obligations")
    ;

    private final String description;
}

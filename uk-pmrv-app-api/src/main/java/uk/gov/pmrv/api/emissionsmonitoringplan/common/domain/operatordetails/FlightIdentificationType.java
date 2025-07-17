package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.operatordetails;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FlightIdentificationType {

    INTERNATIONAL_CIVIL_AVIATION_ORGANISATION("International Civil Aviation Organisation"),
    AIRCRAFT_REGISTRATION_MARKINGS("Aircraft Registration Markings")
    ;

    private final String description;
}

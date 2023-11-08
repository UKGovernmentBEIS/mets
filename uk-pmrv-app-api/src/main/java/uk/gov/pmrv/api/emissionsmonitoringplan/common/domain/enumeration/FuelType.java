package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelType {

    JET_KEROSENE("JET A1 or JET A"),
    JET_GASOLINE("JET B"),
    AVIATION_GASOLINE("AV Gas"),
    OTHER("Do not include sustainable aviation fuel")
    ;

    private final String description;
}

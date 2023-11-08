package uk.gov.pmrv.api.emissionsmonitoringplan.corsia.domain.emissionsources.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelTypeCorsia {

    JET_KEROSENE("JET A1 or JET A"),
    JET_GASOLINE("JET B"),
    AVIATION_GASOLINE("AV Gas"),
    TS_1("TS-1"),
    NO_3_JET_FUEL("No.3 Jet fuel")
    ;

    private final String description;
}
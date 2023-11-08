package uk.gov.pmrv.api.emissionsmonitoringplan.common.domain.emissionsources;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FuelConsumptionMeasuringMethod {

    METHOD_A("Method A"),
    METHOD_B("Method B"),
    BLOCK_ON_BLOCK_OFF("Block οff - Block on"),
    FUEL_UPLIFT("Fuel uplift"),
    BLOCK_HOUR("Block hour");

    private String description;
}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum WasteGasActivity {
    WASTE_GAS_PRODUCED("Waste gas produced"),
    WASTE_GAS_CONSUMED("Waste gas consumed, including safety flaring"),
    WASTE_GAS_FLARED("Waste gas flared, not including safety flaring"),
    WASTE_GAS_IMPORTED("Waste gas imported"),
    WASTE_GAS_EXPORTED("Waste gas exported"),
    NO_WASTE_GAS_ACTIVITIES("No, we do not have any waste gas activities at this sub-installation");

    private final String description;

    public static WasteGasActivity getByValue(String value) {
        return Arrays.stream(values())
                .filter(type -> type.getDescription().equals(value))
                .findFirst()
                .orElse(null);
    }
}

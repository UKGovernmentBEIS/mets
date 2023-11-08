package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionFactorMeasurementUnit {

    TONNES_OF_CO2_PER_TJ("tCO2/TJ"),
    TONNES_OF_CO2_PER_NM3("tCO2/Nm3"),
    TONNES_OF_CO2_PER_TONNE("tCO2/tonne")
    ;

    private final String description;
}

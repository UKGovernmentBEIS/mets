package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CarbonContentMeasurementUnit {

    TONNES_OF_CO2_PER_NM3("tCO2/Nm3"),
    TONNES_OF_CO2_PER_TONNE("tCO2/tonne"),
    TONNES_OF_CARBON_PER_NM3("tC/Nm3"),
    TONNES_OF_CARBON_PER_TONNE("tC/tonne");

    private final String description;
}

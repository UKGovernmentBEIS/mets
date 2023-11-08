package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NCVMeasurementUnit {

    GJ_PER_TONNE("GJ/tonne"),
    GJ_PER_NM3("GJ/Nm3")
    ;

    private final String description;
}

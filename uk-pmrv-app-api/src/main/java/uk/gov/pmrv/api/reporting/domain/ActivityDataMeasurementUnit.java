package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityDataMeasurementUnit {

    TONNES("tonnes"),
    NM3("Nm3")
    ;

    private final String description;
}

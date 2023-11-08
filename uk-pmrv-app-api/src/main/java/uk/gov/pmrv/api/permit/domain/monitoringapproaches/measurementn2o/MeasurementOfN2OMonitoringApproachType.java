package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeasurementOfN2OMonitoringApproachType {
    CALCULATION("Calculation"),
    MEASUREMENT("Measurement");

    private final String description;
}

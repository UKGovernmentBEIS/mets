package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeasurementOfN2OEmissionType {
    ABATED("Abated"),
    UNABATED("Unabated");

    private final String description;
}

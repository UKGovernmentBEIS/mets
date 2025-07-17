package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementBiomassFractionStandardReferenceSourceType {

    LITERATURE_VALUE_AGREED_WITH_UK_ETS_REGULATOR("Literature value agreed with UK ETS regulator"),
    LABORATORY_ANALYSIS("Laboratory analysis"),
    OTHER("Other");

    private String description;
}

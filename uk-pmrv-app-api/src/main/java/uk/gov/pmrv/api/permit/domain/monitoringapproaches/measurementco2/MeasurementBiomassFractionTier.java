package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MeasurementBiomassFractionTier {
    
    NO_TIER("No tier"),
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3");

    private String description;
}

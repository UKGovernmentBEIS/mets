package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeasurementOfN2OMeasuredEmissionsTier {
    
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3");

    private final String description;
}

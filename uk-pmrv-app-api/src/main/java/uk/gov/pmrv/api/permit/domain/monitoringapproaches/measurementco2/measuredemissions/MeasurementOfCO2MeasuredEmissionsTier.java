package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MeasurementOfCO2MeasuredEmissionsTier {
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3"),
    TIER_4("Tier 4");

    private final String description;
}

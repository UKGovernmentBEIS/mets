package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationCarbonContentTier {

    TIER_3("Tier 3"),
    TIER_2B("Tier 2b"),
    TIER_2A("Tier 2a"),
    TIER_1("Tier 1"),
    NO_TIER("No tier");

    private String description;
}

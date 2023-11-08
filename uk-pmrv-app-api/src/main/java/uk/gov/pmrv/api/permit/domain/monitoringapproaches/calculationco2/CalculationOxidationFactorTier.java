package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationOxidationFactorTier {

    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3"),
    NO_TIER("No tier");

    private String description;
}

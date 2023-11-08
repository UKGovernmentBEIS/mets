package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationNetCalorificValueTier {
    
    NO_TIER("No tier"),
    TIER_1("Tier 1"),
    TIER_2A("Tier 2a"),
    TIER_2B("Tier 2b"),
    TIER_3("Tier 3");

    private String description;
}

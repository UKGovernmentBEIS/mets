package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ActivityDataTier {
    TIER_1("Tier 1"),
    TIER_2("Tier 2"),
    TIER_3("Tier 3"),
    TIER_4("Tier 4");

    private final String description;
}

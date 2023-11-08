package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PFCEmissionFactorTier {

    TIER_1("Tier 1"),
    TIER_2("Tier 2");

    private final String description;
}
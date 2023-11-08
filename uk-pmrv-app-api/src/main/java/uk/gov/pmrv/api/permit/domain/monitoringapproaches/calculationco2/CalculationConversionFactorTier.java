package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CalculationConversionFactorTier {

    TIER_2("Tier 2"),
    TIER_1("Tier 1"),
    NO_TIER("No tier");

    private String description;
}

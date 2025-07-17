package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CalculationAnalysisMethodData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#tier != null)}", message = "permit.calculationApproach.tier.emissionFactor.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_3') == (#oneThirdRule != null)}", 
    message = "permit.calculationApproach.tier.emissionFactor.tier_3.oneThirdRule")
@SpELExpression(expression = "{(#tier eq 'TIER_2' || #tier eq 'TIER_2B' || #tier eq 'TIER_2A' || #tier eq 'TIER_1' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
    message = "permit.calculationApproach.tier.emissionFactor.tier_and_highestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied) == (#standardReferenceSource != null)}",
    message = "permit.calculationApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class CalculationEmissionFactor {
    
    private boolean exist;
    
    private CalculationEmissionFactorTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
    private Boolean oneThirdRule;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> oneThirdRuleFiles = new HashSet<>();

    private Boolean defaultValueApplied;

    @Valid
    private CalculationEmissionFactorStandardReferenceSource standardReferenceSource;

    @Valid
    @JsonUnwrapped
    private CalculationAnalysisMethodData calculationAnalysisMethodData;
}

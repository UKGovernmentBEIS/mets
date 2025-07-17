package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'TIER_1') == (#isHighestRequiredTier != null)}", 
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.emissionfactor.isHighestRequiredTier")
public class PFCEmissionFactor {
    
    @NotNull
    private PFCEmissionFactorTier tier;
    
    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
}
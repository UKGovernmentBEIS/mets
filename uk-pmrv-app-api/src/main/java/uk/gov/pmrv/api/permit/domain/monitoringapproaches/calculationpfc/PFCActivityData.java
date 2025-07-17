package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#massBalanceApproachUsed || (#massBalanceApproachUsed == false && (#tier eq 'TIER_1' || #tier eq 'TIER_2'))}",
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.activityData.invalidTier")
@SpELExpression(expression = "{((#tier eq 'TIER_1') || (#massBalanceApproachUsed && (#tier eq 'TIER_2' || #tier eq 'TIER_3'))) == (#isHighestRequiredTier != null)}",
    message = "permit.pfcMonitoringApproach.sourceStreamCategoryAppliedTiers.activityData.isHighestRequiredTier")
public class PFCActivityData {

    private boolean massBalanceApproachUsed;

    @NotNull
    private ActivityDataTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
}

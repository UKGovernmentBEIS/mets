package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'TIER_1' or #tier eq 'TIER_2') == (#isHighestRequiredTier != null)}", 
    message = "permit.n2oMonitoringApproach.n2oMeasuredEmissions.isHighestRequiredTier_tier_1_2")
@SpELExpression(expression = "{(#tier eq 'TIER_3') == (#isHighestRequiredTier == null)}", 
    message = "permit.n2oMonitoringApproach.n2oMeasuredEmissions.isHighestRequiredTier_tier_3")
public class MeasurementOfN2OMeasuredEmissions extends MeasuredEmissions {

    @NotNull
    private MeasurementOfN2OMeasuredEmissionsTier tier;

}

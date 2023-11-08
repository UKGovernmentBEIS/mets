package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasuredEmissions;
import jakarta.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#tier eq 'TIER_1' or #tier eq 'TIER_2' or #tier eq 'TIER_3') == (#isHighestRequiredTier != null)}", 
    message = "permit.measurementMonitoringApproach.measurementMeasuredEmissions.isHighestRequiredTier_tier_1_2_3")
@SpELExpression(expression = "{(#tier eq 'TIER_4') == (#isHighestRequiredTier == null)}",
    message = "permit.measurementMonitoringApproach.measurementMeasuredEmissions.isHighestRequiredTier_tier_4_noTier")
public class MeasurementOfCO2MeasuredEmissions extends MeasuredEmissions {

    @NotNull
    private MeasurementOfCO2MeasuredEmissionsTier tier;

}

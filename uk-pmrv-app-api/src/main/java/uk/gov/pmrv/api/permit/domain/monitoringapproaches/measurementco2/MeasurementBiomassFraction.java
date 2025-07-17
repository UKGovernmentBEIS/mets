package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.MeasurementAnalysisMethodData;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{#exist == (#tier != null)}",
        message = "permit.measurementMonitoringApproach.tier.biomassFraction.exist.tier")
@SpELExpression(expression = "{(#tier eq 'TIER_1' || #tier eq 'TIER_2' || #tier eq 'NO_TIER') == (#isHighestRequiredTier != null)}",
        message = "permit.measurementMonitoringApproach.tier.biomassFraction.isHighestRequiredTier")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#defaultValueApplied) == (#standardReferenceSource != null)}",
    message = "permit.measurementMonitoringApproach.tier.common.defaultValueApplied.standardReferenceSourceType")
public class MeasurementBiomassFraction {

    private boolean exist;

    private MeasurementBiomassFractionTier tier;

    @Valid
    @JsonUnwrapped
    private HighestRequiredTier highestRequiredTier;
    
    private Boolean defaultValueApplied;

    @Valid
    private MeasurementBiomassFractionStandardReferenceSource standardReferenceSource;
    
    @Valid
    @JsonUnwrapped
    private MeasurementAnalysisMethodData calculationAnalysisMethodData;
}

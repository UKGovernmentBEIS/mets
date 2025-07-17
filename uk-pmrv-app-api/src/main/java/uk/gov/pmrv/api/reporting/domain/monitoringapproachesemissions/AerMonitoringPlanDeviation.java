package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.NotNull;

@Data
@SuperBuilder
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#existChangesNotCoveredInApprovedVariations) == (#details != null)}", message = "aer.monitoringplandeviations.details.missing")
public class AerMonitoringPlanDeviation {

    @NotNull
    private Boolean existChangesNotCoveredInApprovedVariations;

    private String details;
}

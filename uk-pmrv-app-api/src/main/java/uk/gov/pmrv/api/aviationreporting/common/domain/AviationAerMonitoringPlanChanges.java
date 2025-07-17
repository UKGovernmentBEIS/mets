package uk.gov.pmrv.api.aviationreporting.common.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#notCoveredChangesExist) == (#details != null)}", message = "aviationAer.monitoringPlanChanges.notCoveredChanges")
public class AviationAerMonitoringPlanChanges {

    @NotNull
    private Boolean notCoveredChangesExist;

    @Size(max = 10000)
    private String details;
}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#furtherInternalSourceStreamsRelevant and #dataSources != null and #dataSources.size() > 0 and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0) or " +
                "(#furtherInternalSourceStreamsRelevant == false and (#dataSources == null or #dataSources.isEmpty()) and (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.direcltyAttributableEmmisions.invalid_input_1"
)
@SpELExpression(
        expression = "{(#transferredCO2ImportedOrExportedRelevant and #amountsMonitoringDescription != null and #amountsMonitoringDescription.trim().length() > 0) or " +
                "(#transferredCO2ImportedOrExportedRelevant == false and (#amountsMonitoringDescription == null or #amountsMonitoringDescription.trim().length() == 0))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.direcltyAttributableEmmisions.invalid_input_2"
)
public class DirectlyAttributableEmissionsPB extends DirectlyAttributableEmissions{

    @NotNull
    private boolean furtherInternalSourceStreamsRelevant;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull ImportedExportedAmountsDataSource> dataSources = new ArrayList<>();

    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @NotNull
    private boolean transferredCO2ImportedOrExportedRelevant;

    @Size(max = 10000)
    private String amountsMonitoringDescription;

}

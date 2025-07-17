package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(
        expression = "{(#measurableHeatExported and " +
                "(#methodologyDeterminationEmissionDescription != null and #methodologyDeterminationEmissionDescription?.trim()?.length() > 0) and " +
                "(#dataSources != null and #dataSources.size() > 0 and #hierarchicalOrder != null) and " +
                "(#methodologyAppliedDescription != null and #methodologyAppliedDescription?.trim()?.length() > 0) ) or " +
                "(#measurableHeatExported == false and " +
                "(#methodologyDeterminationEmissionDescription == null or #methodologyDeterminationEmissionDescription?.trim()?.length() == 0) and " +
                "(#dataSources == null or #dataSources.isEmpty()) and " +
                "(#hierarchicalOrder == null) and " +
                "(#methodologyAppliedDescription == null or #methodologyAppliedDescription?.trim()?.length() == 0) and " +
                "(#supportingFiles == null or #supportingFiles.isEmpty()))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.measurableHeatExported.invalid_input"
)
public class MeasurableHeatExported {

    @NotNull
    private boolean measurableHeatExported;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 1, max = 6)
    private List<@Valid @NotNull MeasurableHeatExportedDataSource> dataSources;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String methodologyAppliedDescription;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String methodologyDeterminationEmissionDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();


}

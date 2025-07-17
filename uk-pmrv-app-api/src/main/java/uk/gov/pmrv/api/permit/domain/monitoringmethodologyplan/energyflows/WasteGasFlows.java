package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;


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
import java.util.ArrayList;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#wasteGasFlowsRelevant and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0 and " +
                "#wasteGasFlowsDataSources != null and #wasteGasFlowsDataSources.size() > 0 and #hierarchicalOrder != null) " +
                " or (#wasteGasFlowsRelevant == false and (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0) and " +
                "(#wasteGasFlowsDataSources == null or #wasteGasFlowsDataSources.isEmpty()) " +
                "and #hierarchicalOrder == null and (#supportingFiles == null or #supportingFiles.isEmpty()))}",
        message = "permit.monitoringmethodologyplans.digitized.energyflows.wasteGasFlows.invalid_input"
)
public class WasteGasFlows {

    @NotNull
    private Boolean wasteGasFlowsRelevant;

    @Size(max = 6)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    List<@Valid @NotNull WasteGasFlowsDataSource> wasteGasFlowsDataSources = new ArrayList<>();

    @Size(max = 10000)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String methodologyAppliedDescription;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

}
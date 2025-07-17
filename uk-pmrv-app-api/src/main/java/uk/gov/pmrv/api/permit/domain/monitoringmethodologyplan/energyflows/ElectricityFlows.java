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

import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#electricityProduced and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0 and " +
                "#electricityFlowsDataSources != null and #electricityFlowsDataSources.size() > 0 and #hierarchicalOrder != null) " +
                " or (#electricityProduced == false and (#methodologyAppliedDescription == null or #methodologyAppliedDescription.trim().length() == 0) and " +
                "(#electricityFlowsDataSources == null or #electricityFlowsDataSources.isEmpty()) " +
                "and #hierarchicalOrder == null and (#supportingFiles == null or #supportingFiles.isEmpty()))}",
        message = "permit.monitoringmethodologyplans.digitized.energyflows.electricityFlows.invalid_input"
)
public class ElectricityFlows {

    @NotNull
    private Boolean electricityProduced;

    @Size(max = 6)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    List<@Valid @NotNull ElectricityFlowsDataSource> electricityFlowsDataSources = new ArrayList<>();

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

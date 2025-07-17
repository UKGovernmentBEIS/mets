package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@SpELExpression(
        expression = "{(#wasteGasActivities != null and #wasteGasActivities.contains('NO_WASTE_GAS_ACTIVITIES') and " +
                "(#dataSources == null or #dataSources?.isEmpty() == true) and " +
                "(#dataSourcesMethodologyAppliedDescription == null or #dataSourcesMethodologyAppliedDescription?.trim()?.length() == 0) and " +
                "(#hierarchicalOrder == null) and " +
                "(#supportingFiles == null or #supportingFiles?.isEmpty()) or " +
                "(#wasteGasActivities != null and !#wasteGasActivities.contains('NO_WASTE_GAS_ACTIVITIES') and " +
                "(#dataSources != null and #dataSources.isEmpty() == false) and " +
                "(#hierarchicalOrder != null) and " +
                "(#dataSourcesMethodologyAppliedDescription != null and #dataSourcesMethodologyAppliedDescription?.trim()?.length() > 0)))}" ,
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.wasteGasBalance.invalid_input_1"
)
@SpELExpression(
        expression = "{#wasteGasActivities != null and #wasteGasActivities.size() == 1 or" +
                "(#wasteGasActivities != null and #wasteGasActivities.size() > 1 and " +
                "!#wasteGasActivities.contains('NO_WASTE_GAS_ACTIVITIES'))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.wasteGasBalance.invalid_input_2"
)
public class WasteGasBalance {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotNull WasteGasActivity> wasteGasActivities = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull WasteGasBalanceEnergyFlowDataSource> dataSources = new ArrayList<>();

    @Size(max = 10000)
    private String dataSourcesMethodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported;

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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(
        expression = "{(#measurableHeatImportedActivities != null and #measurableHeatImportedActivities.contains('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED') and " +
                "(#dataSources == null or #dataSources?.isEmpty() == true) and " +
                "(#methodologyAppliedDescription == null or #methodologyAppliedDescription?.trim()?.length() == 0) and " +
                "(#hierarchicalOrder == null) and " +
                "(#methodologyDeterminationEmissionDescription == null or #methodologyDeterminationEmissionDescription?.trim()?.length() == 0) and " +
                "(#supportingFiles == null or #supportingFiles?.isEmpty()) or " +
                "(#measurableHeatImportedActivities != null and !#measurableHeatImportedActivities.contains('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED') and " +
                "(#hierarchicalOrder != null) and " +
                "(#methodologyDeterminationEmissionDescription != null and #methodologyDeterminationEmissionDescription?.trim()?.length() > 0) and " +
                "(#dataSources != null and #dataSources.isEmpty() == false) and " +
                "(#methodologyAppliedDescription != null and #methodologyAppliedDescription?.trim()?.length() > 0)))}" ,
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.measurableHeatImported.invalid_input_1"
)
@SpELExpression(
        expression = "{#measurableHeatImportedActivities != null and #measurableHeatImportedActivities.size() == 1 or" +
                "(#measurableHeatImportedActivities != null and #measurableHeatImportedActivities.size() > 1 and " +
                "!#measurableHeatImportedActivities.contains('MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED'))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.measurableHeatImported.invalid_input_2"
)
public class MeasurableHeatImported {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotNull MeasurableHeatImportedType> measurableHeatImportedActivities = new HashSet<>();

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull MeasurableHeatImportedDataSource> dataSources;

    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    private String methodologyDeterminationEmissionDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}

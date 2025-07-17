package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat;

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
        expression = "{(#fuelBurnCalculationTypes != null and #fuelBurnCalculationTypes.contains('NO_MEASURABLE_HEAT') and " +
                "(#dataSources == null or #dataSources?.isEmpty() == true) and " +
                "(#dataSourcesMethodologyAppliedDescription == null or #dataSourcesMethodologyAppliedDescription?.trim()?.length() == 0) and " +
                "(#methodologyDeterminationDescription == null or #methodologyDeterminationDescription?.trim()?.length() == 0) and " +
                "(#supportingFiles == null or #supportingFiles?.isEmpty()) and " +
                "(#measurableHeatImportedFromPulp == null or #measurableHeatImportedFromPulp == false)) or " +
                "(#fuelBurnCalculationTypes != null and !#fuelBurnCalculationTypes.contains('NO_MEASURABLE_HEAT') and " +
                "(#dataSources != null and #dataSources.isEmpty() == false) and " +
                "(#dataSourcesMethodologyAppliedDescription != null and #dataSourcesMethodologyAppliedDescription?.trim()?.length() > 0) and " +
                "(#methodologyDeterminationDescription != null and #methodologyDeterminationDescription?.trim()?.length() > 0) and " +
                "#measurableHeatImportedFromPulp != null)}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_1"
)
@SpELExpression(
        expression = "{#fuelBurnCalculationTypes != null and #fuelBurnCalculationTypes.size() == 1 or" +
                "(#fuelBurnCalculationTypes != null and #fuelBurnCalculationTypes.size() > 1 and " +
                "!#fuelBurnCalculationTypes.contains('NO_MEASURABLE_HEAT'))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_2"
)
@SpELExpression(
        expression = "{((#measurableHeatImportedFromPulp == true and " +
                "#pulpMethodologyDeterminationDescription != null and #pulpMethodologyDeterminationDescription?.trim()?.length() > 0) or " +
                "(#measurableHeatImportedFromPulp != true and " +
                "(#pulpMethodologyDeterminationDescription == null or #pulpMethodologyDeterminationDescription?.trim()?.length() == 0)))}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.importedExportedMeasurableeat.invalid_input_3"
)
public class ImportedExportedMeasurableHeat {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonDeserialize(as = LinkedHashSet.class)
    @NotEmpty
    private Set<@NotNull ImportedExportedMeasurableHeatType> fuelBurnCalculationTypes = new HashSet<>();

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull ImportedExportedMeasurableHeatEnergyFlowDataSource> dataSources = new ArrayList<>();

    @Size(max = 10000)
    private String dataSourcesMethodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Size(max = 10000)
    private String methodologyDeterminationDescription;

    private Boolean measurableHeatImportedFromPulp;

    @Size(max = 10000)
    private String pulpMethodologyDeterminationDescription;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}

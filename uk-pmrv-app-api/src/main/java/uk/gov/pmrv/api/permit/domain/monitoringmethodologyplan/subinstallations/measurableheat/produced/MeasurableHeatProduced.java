package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
        expression = "{(#produced and #methodologyAppliedDescription != null and #methodologyAppliedDescription.trim().length() > 0 " +
                "and #hierarchicalOrder!=null and #dataSources!=null and #dataSources.size()>0) or " +
                "(#produced == false and " +
                "(#methodologyAppliedDescription == null or #methodologyAppliedDescription?.trim()?.length() == 0) " +
                "and (#supportingFiles == null or #supportingFiles.isEmpty()) and #hierarchicalOrder==null) and #dataSources ==null}",
        message = "permit.monitoringmethodologyplans.digitized.subinstallation.measurableHeatProduced.invalid_input"
)
public class MeasurableHeatProduced {

    @NotNull
    private boolean produced;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(max = 6)
    private List<@Valid @NotNull MeasurableHeatProducedDataSource> dataSources;

    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuelInputFlows {

    @Size(min = 1, max = 6)
    List<@Valid @NotNull FuelInputDataSource> fuelInputDataSources;

    @Size(max = 10000)
    @NotBlank
    private String methodologyAppliedDescription;

    @NotNull
    @Valid
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();

}

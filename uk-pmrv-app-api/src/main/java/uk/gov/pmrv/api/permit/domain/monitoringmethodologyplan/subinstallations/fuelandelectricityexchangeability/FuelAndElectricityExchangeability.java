package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FuelAndElectricityExchangeability {

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 1, max = 6)
    private List<@Valid @NotNull FuelAndElectricityExchangeabilityEnergyFlowDataSource> fuelAndElectricityExchangeabilityEnergyFlowDataSources = new ArrayList<>();

    @NotBlank
    @Size(max = 10000)
    private String methodologyAppliedDescription;

    @Valid
    @NotNull
    private SubInstallationHierarchicalOrder hierarchicalOrder;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> supportingFiles = new HashSet<>();
}

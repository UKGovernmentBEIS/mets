package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelandelectricityexchangeability;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuelAndElectricityExchangeabilityEnergyFlowDataSource {

    @NotBlank
    @Size(max = 255)
    private String energyFlowDataSourceNo;

    @NotNull
    private AnnexVIISection45 relevantElectricityConsumption;
}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnergyFlows {

    @Valid
    @NotNull
    private FuelInputFlows fuelInputFlows;

    @Valid
    @NotNull
    private MeasurableHeatFlows measurableHeatFlows;

    @Valid
    @NotNull
    private WasteGasFlows wasteGasFlows;

    @Valid
    @NotNull
    private ElectricityFlows electricityFlows;

}

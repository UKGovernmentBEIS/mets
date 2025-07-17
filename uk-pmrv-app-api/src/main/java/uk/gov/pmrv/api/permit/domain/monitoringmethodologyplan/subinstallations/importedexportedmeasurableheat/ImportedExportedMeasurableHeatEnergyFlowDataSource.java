package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImportedExportedMeasurableHeatEnergyFlowDataSource {

    @NotBlank
    @Size(max = 255)
    private String energyFlowDataSourceNo;

    private AnnexVIISection45 measurableHeatImported;

    private AnnexVIISection45 measurableHeatPulp;

    private AnnexVIISection45 measurableHeatNitricAcid;

    private AnnexVIISection45 measurableHeatExported;

    private AnnexVIISection72 netMeasurableHeatFlows;

}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurableHeatExportedDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    private AnnexVIISection45 heatExported;

    private AnnexVIISection72 netMeasurableHeatFlows;

}

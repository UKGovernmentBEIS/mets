package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeasurableHeatProducedDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    @NotNull
    private AnnexVIISection45 heatProduced;

}

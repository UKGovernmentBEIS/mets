package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeasurableHeatFlowsDataSource {

    @NotBlank
    private String dataSourceNumber;

    private AnnexVIISection45 quantification;

    private AnnexVIISection72 net;

}

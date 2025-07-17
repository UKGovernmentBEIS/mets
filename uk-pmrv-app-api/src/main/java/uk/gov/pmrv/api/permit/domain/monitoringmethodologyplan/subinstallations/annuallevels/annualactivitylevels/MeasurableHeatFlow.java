package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels;

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
public class MeasurableHeatFlow {

    @NotBlank
    @Size(max = 255)
    private String measurableHeatFlowQuantificationNo;

    private AnnexVIISection45 quantification;

    private AnnexVIISection72 net;

}

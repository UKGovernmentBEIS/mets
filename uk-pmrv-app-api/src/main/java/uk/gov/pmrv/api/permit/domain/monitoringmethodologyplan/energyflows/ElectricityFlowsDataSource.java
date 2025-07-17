package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ElectricityFlowsDataSource {

    @NotBlank
    private String dataSourceNumber;

    @NotNull
    private AnnexVIISection45 quantification;

}

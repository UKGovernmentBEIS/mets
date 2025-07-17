package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.energyflows;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FuelInputDataSource {

    @NotBlank
    private String dataSourceNumber;

    private AnnexVIISection44 fuelInput;

    private AnnexVIISection46 energyContent;



}

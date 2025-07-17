package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FuelInputDataSourcePB {

    @NotBlank
    @Size(max = 255)
    private String fuelInputDataSourceNo;

    private AnnexVIISection44 fuelInput;

    private AnnexVIISection46 weightedEmissionFactor;
}

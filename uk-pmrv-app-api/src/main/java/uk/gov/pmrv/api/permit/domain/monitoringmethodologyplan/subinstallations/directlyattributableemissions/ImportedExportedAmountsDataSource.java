package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions;

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
public class ImportedExportedAmountsDataSource {

    @NotBlank
    @Size(max = 255)
    private String importedExportedAmountsDataSourceNo;

    private AnnexVIISection44 amounts;

    private AnnexVIISection46 energyContent;

    private AnnexVIISection46 emissionFactorOrCarbonContent;

    private AnnexVIISection46 biomassContent;

}

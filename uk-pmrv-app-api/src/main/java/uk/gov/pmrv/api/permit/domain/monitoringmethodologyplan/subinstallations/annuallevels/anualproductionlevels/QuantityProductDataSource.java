package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.anualproductionlevels;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuantityProductDataSource {

    @NotBlank
    @Size(max = 255)
    private String quantityProductDataSourceNo;

    @NotNull
    private AnnexVIISection44 quantityProduct;
}

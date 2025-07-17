package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthyleneOxideEthyleneGlycolsDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    @NotNull
    @Valid
    private EthyleneOxideEthyleneGlycolsDetails details;
}

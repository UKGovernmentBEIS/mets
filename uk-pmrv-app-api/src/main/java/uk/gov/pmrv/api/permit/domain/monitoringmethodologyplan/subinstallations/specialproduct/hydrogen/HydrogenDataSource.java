package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen;

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
public class HydrogenDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    @NotNull
    @Valid
    private HydrogenDetails details;
}

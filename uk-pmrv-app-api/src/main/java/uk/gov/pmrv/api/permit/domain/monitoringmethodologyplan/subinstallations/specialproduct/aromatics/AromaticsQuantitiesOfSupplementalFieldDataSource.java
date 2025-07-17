package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint2;

import java.util.HashMap;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AromaticsQuantitiesOfSupplementalFieldDataSource {

    @NotBlank
    @Size(max = 255)
    private String dataSourceNo;

    @Builder.Default
    @JsonInclude()
    private Map<@NotNull AnnexIIPoint2, AnnexVIISection44> details = new HashMap<>();
}

package uk.gov.pmrv.api.aviationreporting.corsia.domain.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AviationAerCorsiaVerifiersConclusions {

    @NotBlank
    @Size(max = 10000)
    private String dataQualityMateriality;

    @NotNull
    private MaterialityThresholdType materialityThresholdType;

    @NotNull
    private Boolean materialityThresholdMet;

    @NotBlank
    @Size(max = 10000)
    private String emissionsReportConclusion;

}

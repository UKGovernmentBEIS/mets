package uk.gov.pmrv.api.aviationreporting.ukets.domain.totalemissions;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AviationAerTotalEmissionsConfidentiality {

    @NotNull
    private Boolean confidential;
}

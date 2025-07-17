package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class FuelInputDataSourceFA {

    @NotBlank
    @Size(max = 255)
    private String fuelInputDataSourceNo;

    private AnnexVIISection44 fuelInput;

    private AnnexVIISection46 netCalorificValue;

    private AnnexVIISection46 weightedEmissionFactor;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private AnnexVIISection44 wasteGasFuelInput;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private AnnexVIISection46 wasteGasNetCalorificValue;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private AnnexVIISection46 emissionFactor;
}

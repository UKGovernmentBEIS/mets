package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class SynthesisGasDetails {

    @JsonProperty("SYNTHESIS_GAS_TOTAL_PRODUCTION")
    private AnnexVIISection44 totalProduction;

    @JsonProperty("SYNTHESIS_GAS_COMPOSITION_DATA")
    private AnnexVIISection46 compositionData;
}

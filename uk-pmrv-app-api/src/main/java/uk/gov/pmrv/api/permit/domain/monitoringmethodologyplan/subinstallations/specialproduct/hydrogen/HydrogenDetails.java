package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen;

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
public class HydrogenDetails {

    @JsonProperty("HYDROGEN_TOTAL_PRODUCTION")
    private AnnexVIISection44 totalProduction;

    @JsonProperty("HYDROGEN_VOLUME_FRACTION")
    private AnnexVIISection46 volumeFraction;
}

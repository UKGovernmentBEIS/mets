package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.ethyleneoxideethyleneglycols;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EthyleneOxideEthyleneGlycolsDetails {

    @JsonProperty("ETHYLEN_OXIDE")
    private AnnexVIISection44 ethyleneOxide;

    @JsonProperty("MONOTHYLENE_GLYCOL")
    private AnnexVIISection44 monothyleneGlycol;

    @JsonProperty("DIETHYLENE_GLYCOL")
    private AnnexVIISection44 diethyleneGlycol;

    @JsonProperty("TRIETHYLENE_GLYCOL")
    private AnnexVIISection44 triethyleneGlycol;
}

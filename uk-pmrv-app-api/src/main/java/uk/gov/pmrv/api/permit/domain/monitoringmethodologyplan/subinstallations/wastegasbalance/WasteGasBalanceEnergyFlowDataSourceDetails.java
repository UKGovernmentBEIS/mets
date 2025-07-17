package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.wastegasbalance;

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
public class WasteGasBalanceEnergyFlowDataSourceDetails {

    private AnnexVIISection44 entity;

    private AnnexVIISection46 energyContent;

    private AnnexVIISection46 emissionFactor;

}

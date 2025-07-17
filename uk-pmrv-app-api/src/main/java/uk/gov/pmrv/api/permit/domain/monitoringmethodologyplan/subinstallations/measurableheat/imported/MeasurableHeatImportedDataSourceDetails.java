package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection45;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeasurableHeatImportedDataSourceDetails {

    private AnnexVIISection45 entity;

    private AnnexVIISection72 netContent;

}

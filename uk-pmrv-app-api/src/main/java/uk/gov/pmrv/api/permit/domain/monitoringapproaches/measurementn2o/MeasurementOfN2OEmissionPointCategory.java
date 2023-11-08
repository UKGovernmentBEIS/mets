package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.EmissionPointBase;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementOfN2OEmissionPointCategory extends EmissionPointBase {

    @NotNull
    private MeasurementOfN2OEmissionType emissionType;

    @NotNull
    private MeasurementOfN2OMonitoringApproachType monitoringApproachType;

    @Valid
    private TransferN2O transfer;
}

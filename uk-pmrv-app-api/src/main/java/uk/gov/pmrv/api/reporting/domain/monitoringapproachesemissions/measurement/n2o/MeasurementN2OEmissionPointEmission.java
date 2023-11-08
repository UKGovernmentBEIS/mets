package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementN2OEmissionPointEmission extends MeasurementEmissionPointEmission {

    @NotNull
    private MeasurementOfN2OMeasuredEmissionsTier tier;
}

package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementOfN2OEmissionPointCategoryAppliedTier {

    @Valid
    @NotNull
    private MeasurementOfN2OEmissionPointCategory emissionPointCategory;

    @Valid
    @NotNull
    private MeasurementOfN2OMeasuredEmissions measuredEmissions;

    @Valid
    @NotNull
    private AppliedStandard appliedStandard;
}

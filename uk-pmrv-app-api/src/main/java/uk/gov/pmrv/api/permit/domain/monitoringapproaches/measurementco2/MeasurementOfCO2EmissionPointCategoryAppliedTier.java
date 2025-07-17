package uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementOfCO2EmissionPointCategoryAppliedTier {

    @Valid
    @NotNull
    private MeasurementOfCO2EmissionPointCategory emissionPointCategory;

    @Valid
    @NotNull
    private MeasurementOfCO2MeasuredEmissions measuredEmissions;

    @Valid
    @NotNull
    private AppliedStandard appliedStandard;

    @Valid
    private MeasurementBiomassFraction biomassFraction;
}

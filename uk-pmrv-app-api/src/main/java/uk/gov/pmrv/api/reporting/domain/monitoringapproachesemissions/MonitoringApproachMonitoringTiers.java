package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PermitOriginatedCalculationPfcParameterMonitoringTier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringApproachMonitoringTiers {

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<String, @NotEmpty List<CalculationParameterMonitoringTier>> calculationSourceStreamParamMonitoringTiers = new HashMap<>();

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<String, MeasurementOfCO2MeasuredEmissionsTier> measurementCO2EmissionPointParamMonitoringTiers = new HashMap<>();

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<String, MeasurementOfN2OMeasuredEmissionsTier> measurementN2OEmissionPointParamMonitoringTiers = new HashMap<>();

    @Valid
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private Map<String, PermitOriginatedCalculationPfcParameterMonitoringTier> calculationPfcSourceStreamParamMonitoringTiers = new HashMap<>();
}

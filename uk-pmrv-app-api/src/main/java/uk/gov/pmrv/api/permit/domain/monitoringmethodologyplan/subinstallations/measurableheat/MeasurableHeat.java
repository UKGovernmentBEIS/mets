package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.produced.MeasurableHeatProduced;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeasurableHeat {

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MeasurableHeatProduced measurableHeatProduced;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MeasurableHeatImported measurableHeatImported;

    @Valid
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private MeasurableHeatExported measurableHeatExported;

    public static List<SubInstallationType> getMeasurableHeatProducedSupportingSubInstallationTypes() {
        return List.of(SubInstallationType.HEAT_BENCHMARK_CL, SubInstallationType.HEAT_BENCHMARK_NON_CL, SubInstallationType.DISTRICT_HEATING_NON_CL);
    }

    public static List<SubInstallationType> getMeasurableHeatImportedSupportingSubInstallationTypes() {
        return List.of(SubInstallationType.HEAT_BENCHMARK_CL, SubInstallationType.HEAT_BENCHMARK_NON_CL, SubInstallationType.DISTRICT_HEATING_NON_CL);
    }

    public static List<SubInstallationType> getMeasurableHeatExportedSupportingSubInstallationTypes() {
        return List.of(SubInstallationType.FUEL_BENCHMARK_CL, SubInstallationType.FUEL_BENCHMARK_NON_CL);
    }
}

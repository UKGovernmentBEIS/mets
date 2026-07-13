package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
public class DirectlyAttributableEmissionsFA extends DirectlyAttributableEmissions{

    public static List<SubInstallationType> getSupportedSubInstallationTypes() {
        return List.of(SubInstallationType.HEAT_BENCHMARK_CL,
                SubInstallationType.HEAT_BENCHMARK_NON_CL,
                SubInstallationType.HEAT_BENCHMARK_CL_CBAM,
                SubInstallationType.HEAT_BENCHMARK_CL_NON_CBAM,
                SubInstallationType.DISTRICT_HEATING_NON_CL,
                SubInstallationType.FUEL_BENCHMARK_CL,
                SubInstallationType.FUEL_BENCHMARK_NON_CL,
                SubInstallationType.FUEL_BENCHMARK_CL_CBAM,
                SubInstallationType.FUEL_BENCHMARK_CL_NON_CBAM);
    }
}

package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
public class DirectlyAttributableEmissionsFA extends DirectlyAttributableEmissions{

    public static List<SubInstallationType> getSupportedSubInstallationTypes() {
        return List.of(SubInstallationType.HEAT_BENCHMARK_CL,
                SubInstallationType.HEAT_BENCHMARK_NON_CL,
                SubInstallationType.DISTRICT_HEATING_NON_CL,
                SubInstallationType.FUEL_BENCHMARK_CL,
                SubInstallationType.FUEL_BENCHMARK_NON_CL);
    }
}

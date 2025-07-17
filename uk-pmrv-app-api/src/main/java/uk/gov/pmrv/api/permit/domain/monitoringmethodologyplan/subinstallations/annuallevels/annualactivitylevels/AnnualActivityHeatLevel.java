package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnualActivityHeatLevel extends AnnualActivityLevel {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Size(min = 1, max = 6)
    private List<@Valid @NotNull MeasurableHeatFlow> measurableHeatFlowList;

    @Override
    @JsonIgnore
    public List<SubInstallationType> getSupportedSubInstallationTypes() {
        return new ArrayList<>(List.of(SubInstallationType.HEAT_BENCHMARK_CL,SubInstallationType.HEAT_BENCHMARK_NON_CL,SubInstallationType.DISTRICT_HEATING_NON_CL));
    }

}

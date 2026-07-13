package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@NoArgsConstructor
public class AnnualActivityProcessLevel extends AnnualActivityLevel {

    @JsonIgnore
    public List<SubInstallationType> getSupportedSubInstallationTypes() {
        return new ArrayList<>(List.of(SubInstallationType.PROCESS_EMISSIONS_CL,
                SubInstallationType.PROCESS_EMISSIONS_NON_CL,
                SubInstallationType.PROCESS_EMISSIONS_CL_CBAM,
                SubInstallationType.PROCESS_EMISSIONS_CL_NON_CBAM));
    }
}

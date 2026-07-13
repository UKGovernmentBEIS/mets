package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.AnnualLevel;

import java.util.List;

@SuperBuilder
@NoArgsConstructor
public abstract class AnnualActivityLevel extends AnnualLevel {
    @JsonIgnore
    public abstract List<SubInstallationType> getSupportedSubInstallationTypes();
}

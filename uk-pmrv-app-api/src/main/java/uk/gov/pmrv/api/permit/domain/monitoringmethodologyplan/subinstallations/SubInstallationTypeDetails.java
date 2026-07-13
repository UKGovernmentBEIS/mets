package uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubInstallationTypeDetails {

    private SubInstallationType subInstallationType;
    private boolean isCoveredByUKCBAM;
    private boolean isValid;
}

package uk.gov.pmrv.api.permit.service.monitoringmethodologyplan;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationTypeDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubInstallationService {

    private final ConfigurationService configurationService;
    private static final String CBAM_TRANSITION_TOGGLE = "sub_installation_types.cbam.transition.toggle";


    public List<SubInstallationTypeDetails> getSubInstallationTypesDetails() {
        Optional<ConfigurationDTO> cbamTransitionToggleConfiguration = configurationService
                .getConfigurationByKey(CBAM_TRANSITION_TOGGLE);

        boolean cbamTransitionToggle = cbamTransitionToggleConfiguration
                .map(ConfigurationDTO::getValue)
                .filter(Boolean.class::isInstance)
                .map(Boolean.class::cast)
                .orElse(false);

        return Arrays.stream(SubInstallationType.values())
                .map(subInstallationType -> SubInstallationTypeDetails.builder()
                        .subInstallationType(subInstallationType)
                        .isCoveredByUKCBAM(subInstallationType.isCoveredByUKCBAM())
                        .isValid(subInstallationType.getValidityPeriod().isValid(cbamTransitionToggle))
                        .build())
                .toList();
    }
}

package uk.gov.pmrv.api.permit.service.monitoringmethodologyplan;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationTypeDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SubInstallationServiceTest {

    private static final String CBAM_TRANSITION_TOGGLE = "sub_installation_types.cbam.transition.toggle";

    @Mock
    private ConfigurationService configurationService;

    @InjectMocks
    private SubInstallationService subInstallationService;

    @Test
    void getSubInstallationTypesDetails_whenCbamTransitionToggleIsFalse() {
        when(configurationService.getConfigurationByKey(CBAM_TRANSITION_TOGGLE))
                .thenReturn(Optional.of(ConfigurationDTO.builder()
                        .value(false)
                        .build()));

        List<SubInstallationTypeDetails> result =
                subInstallationService.getSubInstallationTypesDetails();

        SubInstallationTypeDetails hydrogen = find(result, SubInstallationType.HYDROGEN);
        SubInstallationTypeDetails hydrogenCbam = find(result, SubInstallationType.HYDROGEN_CBAM);
        SubInstallationTypeDetails ammonia = find(result, SubInstallationType.AMMONIA);

        boolean hydrogenExpectedValid = !LocalDate.now().isAfter(java.time.LocalDate.of(2026, 12, 31));
        boolean hydrogenCbamExpectedValid = !LocalDate.now().isBefore(java.time.LocalDate.of(2027, 1, 1));
        assertThat(hydrogen.isValid()).isEqualTo(hydrogenExpectedValid);
        assertThat(hydrogen.isCoveredByUKCBAM()).isFalse();

        assertThat(hydrogenCbam.isValid()).isEqualTo(hydrogenCbamExpectedValid);
        assertThat(hydrogenCbam.isCoveredByUKCBAM()).isTrue();

        assertThat(ammonia.isValid()).isTrue();
        assertThat(ammonia.isCoveredByUKCBAM()).isTrue();

        assertThat(result).hasSize(SubInstallationType.values().length);
    }

    @Test
    void getSubInstallationTypesDetails_whenCbamTransitionToggleIsTrue() {
        when(configurationService.getConfigurationByKey(CBAM_TRANSITION_TOGGLE))
                .thenReturn(Optional.of(ConfigurationDTO.builder()
                        .value(true)
                        .build()));

        List<SubInstallationTypeDetails> result =
                subInstallationService.getSubInstallationTypesDetails();

        SubInstallationTypeDetails hydrogen = find(result, SubInstallationType.HYDROGEN);
        SubInstallationTypeDetails hydrogenCbam = find(result, SubInstallationType.HYDROGEN_CBAM);
        SubInstallationTypeDetails ammonia = find(result, SubInstallationType.AMMONIA);

        assertThat(hydrogen.isValid()).isFalse();
        assertThat(hydrogen.isCoveredByUKCBAM()).isFalse();

        assertThat(hydrogenCbam.isValid()).isTrue();
        assertThat(hydrogenCbam.isCoveredByUKCBAM()).isTrue();

        assertThat(ammonia.isValid()).isTrue();
        assertThat(ammonia.isCoveredByUKCBAM()).isTrue();

        assertThat(result).hasSize(SubInstallationType.values().length);
    }

    @Test
    void getSubInstallationTypesDetails_whenCbamTransitionToggleConfigurationIsMissing_defaultsToFalse() {
        when(configurationService.getConfigurationByKey(CBAM_TRANSITION_TOGGLE))
                .thenReturn(Optional.empty());

        List<SubInstallationTypeDetails> result =
                subInstallationService.getSubInstallationTypesDetails();

        SubInstallationTypeDetails hydrogen = find(result, SubInstallationType.HYDROGEN);
        SubInstallationTypeDetails hydrogenCbam = find(result, SubInstallationType.HYDROGEN_CBAM);

        assertThat(hydrogen.isValid()).isEqualTo(!LocalDate.now().isAfter(java.time.LocalDate.of(2026, 12, 31)));
        assertThat(hydrogenCbam.isValid()).isEqualTo(!LocalDate.now().isBefore(java.time.LocalDate.of(2027, 1, 1)));

        assertThat(result).hasSize(SubInstallationType.values().length);
    }

    private SubInstallationTypeDetails find(
            List<SubInstallationTypeDetails> result,
            SubInstallationType subInstallationType) {

        return result.stream()
                .filter(details -> details.getSubInstallationType() == subInstallationType)
                .findFirst()
                .orElseThrow();
    }
}

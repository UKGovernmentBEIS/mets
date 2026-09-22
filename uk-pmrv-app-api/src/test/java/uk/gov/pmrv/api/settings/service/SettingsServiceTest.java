package uk.gov.pmrv.api.settings.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.SettingsSection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettingsServiceTest {

    @InjectMocks
    private SettingsService service;

    @Mock
    private SettingsFeeAuthorizationService settingsFeeAuthorizationService;

    @Mock
    private AppUser appUser;

    @Test
    void getAccessibleSections_installation_withFeeViewPermission_returnsFees() {
        when(settingsFeeAuthorizationService.canView(appUser)).thenReturn(true);

        List<SettingsSection> result = service.getAccessibleSections(appUser, AccountType.INSTALLATION);

        assertThat(result).containsExactly(SettingsSection.FEES);
    }

    @Test
    void getAccessibleSections_aviation_withFeeViewPermission_returnsFees() {
        when(settingsFeeAuthorizationService.canView(appUser)).thenReturn(true);

        List<SettingsSection> result = service.getAccessibleSections(appUser, AccountType.AVIATION);

        assertThat(result).containsExactly(SettingsSection.FEES);
    }

    @Test
    void getAccessibleSections_withNoPermissions_returnsEmpty() {
        when(settingsFeeAuthorizationService.canView(appUser)).thenReturn(false);

        List<SettingsSection> result = service.getAccessibleSections(appUser, AccountType.INSTALLATION);

        assertThat(result).isEmpty();
    }
}

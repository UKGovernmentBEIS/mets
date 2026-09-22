package uk.gov.pmrv.api.settings.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.rules.domain.PmrvScope.SETTINGS_PAGE_FEES_VIEW;

@ExtendWith(MockitoExtension.class)
class SettingsFeeAuthorizationServiceTest {

    @InjectMocks
    private SettingsFeeAuthorizationService service;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    @Mock
    private AppUser appUser;

    @Test
    void canView_returnsTrue_whenUserHasViewScope() {
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, SETTINGS_PAGE_FEES_VIEW)).thenReturn(true);

        assertThat(service.canView(appUser)).isTrue();
    }

    @Test
    void canView_returnsFalse_whenUserHasNoScope() {
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, SETTINGS_PAGE_FEES_VIEW)).thenReturn(false);

        assertThat(service.canView(appUser)).isFalse();
    }
}

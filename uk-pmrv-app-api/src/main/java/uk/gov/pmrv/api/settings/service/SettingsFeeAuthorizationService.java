package uk.gov.pmrv.api.settings.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;

import static uk.gov.pmrv.api.authorization.rules.domain.PmrvScope.SETTINGS_PAGE_FEES_VIEW;

@Service
@RequiredArgsConstructor
public class SettingsFeeAuthorizationService {

    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;

    public boolean canView(AppUser appUser) {
        return compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, SETTINGS_PAGE_FEES_VIEW);
    }
}

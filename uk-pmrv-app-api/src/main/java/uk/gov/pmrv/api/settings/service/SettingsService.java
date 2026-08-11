package uk.gov.pmrv.api.settings.service;

import java.util.List;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.settings.domain.SettingsSection;

@Service
public class SettingsService {

    public List<SettingsSection> getAccessibleSections(AppUser appUser, AccountType accountType) {
        // TODO: Implement logic to determine accessible sections based on user and account type
        return List.of(SettingsSection.values());
    }
}

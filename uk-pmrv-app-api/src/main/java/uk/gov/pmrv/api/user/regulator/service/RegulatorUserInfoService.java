package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.Scope;
import uk.gov.netz.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserInfoDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulatorUserInfoService {

    private final CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    private final UserAuthService userAuthService;

    public List<RegulatorUserInfoDTO> getRegulatorUsersInfo(AppUser appUser, List<String> userIds) {
        boolean hasEditUserScopeOnCa = compAuthAuthorizationResourceService.hasUserScopeToCompAuth(appUser, Scope.EDIT_USER);

        List<RegulatorUserInfoDTO> regulatorsUserInfo = userAuthService.getUsersWithAttributes(userIds, RegulatorUserInfoDTO.class);
        if(!hasEditUserScopeOnCa) {
            for (RegulatorUserInfoDTO regulatorUserInfo: regulatorsUserInfo) {
                regulatorUserInfo.setEnabled(null);
            }
        }
        return regulatorsUserInfo;
    }
}

package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.mireport.system.accountuserscontacts.OperatorUserInfoDTO;
import uk.gov.pmrv.api.user.core.service.UserInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorUserInfoService {

    private final UserInfoService userInfoService;

    public List<OperatorUserInfoDTO> getOperatorUsersInfo(List<String> userIds) {
        return userInfoService.getOperatorUsersInfo(userIds);
    }
}

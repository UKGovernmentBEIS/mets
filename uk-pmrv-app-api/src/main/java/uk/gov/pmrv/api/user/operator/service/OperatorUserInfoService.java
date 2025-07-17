package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.UserInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperatorUserInfoService {

    private final UserInfoService userInfoService;

    public List<UserInfoDTO> getOperatorUsersInfo(List<String> userIds) {
        return userInfoService.getUsersInfo(userIds);
    }
}

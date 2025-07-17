package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.UserInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VerifierUserInfoService {

    private final UserInfoService userInfoService;

    public List<UserInfoDTO> getVerifierUsersInfo(List<String> userIds) {
        return userInfoService.getUsersInfo(userIds);
    }

}

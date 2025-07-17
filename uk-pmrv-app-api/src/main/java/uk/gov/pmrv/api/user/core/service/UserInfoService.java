package uk.gov.pmrv.api.user.core.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.core.transform.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInfoService {

    private final UserAuthService userAuthService;
    private static final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public List<UserInfoDTO> getUsersInfo(List<String> userIds) {
        return userAuthService.getUsers(userIds).stream()
            .collect(Collectors.toMap(UserInfo::getId, user -> user))
            .values().stream()
            .map(userMapper::toUserInfoDTO).collect(Collectors.toList());
    }
}

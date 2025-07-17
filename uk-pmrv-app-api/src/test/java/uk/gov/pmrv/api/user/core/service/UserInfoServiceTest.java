package uk.gov.pmrv.api.user.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserInfoServiceTest {

    @InjectMocks
    private UserInfoService userInfoService;

    @Mock
    private UserAuthService userAuthService;

    @Test
    void getUsersInfo() {
        String userId = "userId";
        String fn = "fn";
        String ln = "ln";

        UserInfo userInfo = UserInfo.builder().id(userId).firstName(fn).lastName(ln).enabled(true).build();
        List<UserInfoDTO> expectedUserInfoList = List.of(
            UserInfoDTO.builder().userId(userId).firstName(fn).lastName(ln).enabled(true).build()
        );

        when(userAuthService.getUsers(List.of(userId))).thenReturn(List.of(userInfo));

        List<UserInfoDTO> actualUserInfoList = userInfoService
            .getUsersInfo(List.of(userId));

        assertThat(actualUserInfoList).containsExactlyElementsOf(expectedUserInfoList);

        verify(userAuthService, times(1)).getUsers(List.of(userId));
    }
    
}
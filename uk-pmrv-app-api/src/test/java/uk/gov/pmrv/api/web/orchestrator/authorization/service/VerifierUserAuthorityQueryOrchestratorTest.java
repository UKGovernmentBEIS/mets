package uk.gov.pmrv.api.web.orchestrator.authorization.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.AuthorityStatus;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthoritiesDTO;
import uk.gov.netz.api.authorization.core.domain.dto.UserAuthorityDTO;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityQueryService;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInfoService;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UserAuthorityInfoDTO;
import uk.gov.pmrv.api.web.orchestrator.authorization.dto.UsersAuthoritiesInfoDTO;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.core.domain.AuthorityStatus.ACTIVE;

@ExtendWith(MockitoExtension.class)
class VerifierUserAuthorityQueryOrchestratorTest {

    @InjectMocks
    private VerifierUserAuthorityQueryOrchestrator verifierUserAuthorityQueryOrchestrator;

    @Mock
    private VerifierAuthorityQueryService verifierAuthorityQueryService;

    @Mock
    private VerifierUserInfoService verifierUserInfoService;


    @Test
    void getVerifierAuthorities() {
        Long vbId = 1L;
        String userId = "userId";
        AuthorityStatus status = ACTIVE;
        AppUser authUser = AppUser.builder()
            .userId("authUserId")
            .authorities(List.of(AppAuthority.builder().verificationBodyId(vbId).build()))
            .build();
        UserAuthorityDTO userAuthority = UserAuthorityDTO.builder()
            .userId(userId)
            .authorityStatus(status)
            .build();
        UserAuthoritiesDTO userAuthorities = UserAuthoritiesDTO.builder()
            .authorities(List.of(userAuthority))
            .editable(true)
            .build();
        UserInfoDTO userInfo = UserInfoDTO.builder().userId(userId).build();

        UserAuthorityInfoDTO expectedUserAuthInfo =
            UserAuthorityInfoDTO.builder().userId(userId).authorityStatus(status).build();

        when(verifierAuthorityQueryService.getVerifierAuthorities(authUser)).thenReturn(userAuthorities);
        when(verifierUserInfoService.getVerifierUsersInfo(List.of(userId))).thenReturn(List.of(userInfo));

        UsersAuthoritiesInfoDTO actualUserAuthInfo = verifierUserAuthorityQueryOrchestrator.getVerifierUsersAuthoritiesInfo(authUser);

        assertTrue(actualUserAuthInfo.isEditable());
        assertThat(actualUserAuthInfo.getAuthorities()).hasSize(1);
        assertEquals(expectedUserAuthInfo, actualUserAuthInfo.getAuthorities().get(0));

        verify(verifierAuthorityQueryService, times(1)).getVerifierAuthorities(authUser);
        verify(verifierUserInfoService, times(1)).getVerifierUsersInfo(List.of(userId));
    }

    @Test
    void getVerifierAuthoritiesByVerificationBodyId() {
        Long vbId = 1L;
        String userId = "userId";
        AuthorityStatus status = ACTIVE;
        UserAuthorityDTO userAuthority = UserAuthorityDTO.builder()
            .userId(userId)
            .authorityStatus(status)
            .build();
        UserAuthoritiesDTO userAuthorities = UserAuthoritiesDTO.builder()
            .authorities(List.of(userAuthority))
            .editable(true)
            .build();
        UserInfoDTO userInfo = UserInfoDTO.builder().userId(userId).build();

        UserAuthorityInfoDTO expectedUserAuthInfo =
            UserAuthorityInfoDTO.builder().userId(userId).authorityStatus(status).build();

        when(verifierAuthorityQueryService.getVerificationBodyAuthorities(vbId, true)).thenReturn(userAuthorities);
        when(verifierUserInfoService.getVerifierUsersInfo(List.of(userId))).thenReturn(List.of(userInfo));

        UsersAuthoritiesInfoDTO actualUserAuthInfo = verifierUserAuthorityQueryOrchestrator.getVerifierAuthoritiesByVerificationBodyId(vbId);

        assertTrue(actualUserAuthInfo.isEditable());
        assertThat(actualUserAuthInfo.getAuthorities()).hasSize(1);
        assertEquals(expectedUserAuthInfo, actualUserAuthInfo.getAuthorities().get(0));

        verify(verifierAuthorityQueryService, times(1)).getVerificationBodyAuthorities(vbId, true);
        verify(verifierUserInfoService, times(1)).getVerifierUsersInfo(List.of(userId));
    }
}
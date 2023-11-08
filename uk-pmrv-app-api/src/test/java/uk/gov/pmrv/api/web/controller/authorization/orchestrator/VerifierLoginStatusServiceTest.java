package uk.gov.pmrv.api.web.controller.authorization.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.AuthenticationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.LoginStatus;
import uk.gov.pmrv.api.web.controller.authorization.orchestrator.dto.UserDomainsLoginStatusInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACCEPTED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.DISABLED;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.PENDING;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.TEMP_DISABLED;

@ExtendWith(MockitoExtension.class)
class VerifierLoginStatusServiceTest {

    @InjectMocks
    private VerifierLoginStatusService verifierLoginStatusService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private UserAuthService userAuthService;


    @Test
    void getUserDomainsLoginStatusInfo_when_active_authorities_with_permissions_then_enabled() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(ACTIVE)
            .build();
        authority.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.ENABLED,
                AccountType.AVIATION, LoginStatus.ENABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_active_authorities_without_permissions_then_no_authority() {
        String userId = "userId";
        Authority authority = Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(ACTIVE)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY,
                AccountType.AVIATION, LoginStatus.NO_AUTHORITY
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_two_active_authorities_only_one_with_permissions_then_enabled() {
        String userId = "userId";
        Authority authority1 =  Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(ACTIVE)
            .build();
        authority1.setAuthorityPermissions(List.of(AuthorityPermission.builder().build()));

        Authority authority2 =  Authority.builder()
            .userId(userId)
            .verificationBodyId(2L)
            .status(ACTIVE)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.ENABLED,
                AccountType.AVIATION, LoginStatus.ENABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_no_active_authorities_but_temp_disabled_then_temp_disabled() {
        String userId = "userId";
        Authority authority1 = Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(TEMP_DISABLED)
            .build();
        Authority authority2 = Authority.builder()
            .userId(userId)
            .verificationBodyId(2L)
            .status(DISABLED)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.TEMP_DISABLED,
                AccountType.AVIATION, LoginStatus.TEMP_DISABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_nor_active_authorities_neither_temp_disabled_then_disabled() {
        String userId = "userId";
        Authority authority1 = Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(DISABLED)
            .build();
        Authority authority2 = Authority.builder()
            .userId(userId)
            .verificationBodyId(2L)
            .status(PENDING)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority1, authority2));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.DISABLED,
                AccountType.AVIATION, LoginStatus.DISABLED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_nor_active_authorities_and_accepted() {
        final String userId = "userId";

        Authority authority = Authority.builder()
            .userId(userId)
            .verificationBodyId(1L)
            .status(ACCEPTED)
            .build();

        when(authorityRepository.findByUserId(userId)).thenReturn(List.of(authority));

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.ACCEPTED,
                AccountType.AVIATION, LoginStatus.ACCEPTED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verifyNoInteractions(userAuthService);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_no_authorities_and_auth_status_not_deleted_then_no_authority() {
        final String userId = "userId";
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder().status(AuthenticationStatus.REGISTERED).build();

        when(authorityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfoDTO);

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.NO_AUTHORITY,
                AccountType.AVIATION, LoginStatus.NO_AUTHORITY
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(userAuthService, times(1)).getUserByUserId(userId);
    }

    @Test
    void getUserDomainsLoginStatusInfo_when_no_authorities_and_auth_status_is_deleted_then_deleted() {
        final String userId = "userId";
        final UserInfoDTO userInfoDTO = UserInfoDTO.builder().status(AuthenticationStatus.DELETED).build();

        when(authorityRepository.findByUserId(userId)).thenReturn(Collections.emptyList());
        when(userAuthService.getUserByUserId(userId)).thenReturn(userInfoDTO);

        UserDomainsLoginStatusInfo userDomainsLoginStatusInfo = verifierLoginStatusService.getUserDomainsLoginStatusInfo(userId);

        assertThat(userDomainsLoginStatusInfo.getDomainsLoginStatuses()).containsExactlyInAnyOrderEntriesOf(
            Map.of(
                AccountType.INSTALLATION, LoginStatus.DELETED,
                AccountType.AVIATION, LoginStatus.DELETED
            ));
        verify(authorityRepository, times(1)).findByUserId(userId);
        verify(userAuthService, times(1)).getUserByUserId(userId);
    }

    @Test
    void getRoleType() {
        assertEquals(RoleType.VERIFIER, verifierLoginStatusService.getRoleType());
    }
}
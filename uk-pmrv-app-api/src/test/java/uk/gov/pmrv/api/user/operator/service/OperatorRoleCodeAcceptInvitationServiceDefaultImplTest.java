package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.service.RoleService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserWithAuthorityDTO;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.netz.api.authorization.AuthorityConstants.EMITTER_CONTACT;
import static uk.gov.netz.api.authorization.AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;

@ExtendWith(MockitoExtension.class)
class OperatorRoleCodeAcceptInvitationServiceDefaultImplTest {

    @InjectMocks
    private OperatorRoleCodeAcceptInvitationServiceDefaultImpl service;

    @Mock
    private RoleService roleService;

    @Mock
    private UserAuthService userAuthService;
    
    @Mock
    private OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    @Test
    void acceptInvitation_user_not_enabled() {
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder()
            .enabled(false)
            .build();

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.PENDING_TO_REGISTERED_SET_REGISTER_FORM, userInvitationStatus);

        verifyNoInteractions(operatorUserRegisteredAcceptInvitationService, userAuthService);
    }

    @Test
    void acceptInvitation_user_enabled_and_has_password() {
        Long authorityId = 1L;
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder()
            .enabled(true)
            .userId("userId")
            .userAuthorityId(authorityId)
            .build();

        when(userAuthService.hasUserPassword(operatorUserAcceptInvitation.getUserId())).thenReturn(true);

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.ACCEPTED, userInvitationStatus);

        verify(userAuthService, times(1))
                .hasUserPassword(operatorUserAcceptInvitation.getUserId());
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
                .acceptAuthorityAndNotify(authorityId);
    }

    @Test
    void acceptInvitation_user_enabled_and_no_password() {
        Long authorityId = 1L;
        String userId = "userId";
        OperatorUserWithAuthorityDTO operatorUserAcceptInvitation = OperatorUserWithAuthorityDTO.builder()
            .enabled(true)
            .userId(userId)
            .userAuthorityId(authorityId)
            .build();

        when(userAuthService.hasUserPassword(operatorUserAcceptInvitation.getUserId())).thenReturn(false);

        UserInvitationStatus userInvitationStatus = service.acceptInvitation(operatorUserAcceptInvitation);

        assertEquals(UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY, userInvitationStatus);
		verify(userAuthService, times(1)).hasUserPassword(operatorUserAcceptInvitation.getUserId());
        verifyNoInteractions(operatorUserRegisteredAcceptInvitationService);
        verifyNoMoreInteractions(userAuthService);
    }

    @Test
    void getRoleCodes() {
        Set<String> operatorRoleCodes =
            Set.of(OPERATOR_ADMIN_ROLE_CODE, EMITTER_CONTACT);

        when(roleService.getCodesByType(RoleTypeConstants.OPERATOR)).thenReturn(operatorRoleCodes);

        Set<String> roleCodes = service.getRoleCodes();

        assertThat(roleCodes).containsOnly(OPERATOR_ADMIN_ROLE_CODE);
    }
}
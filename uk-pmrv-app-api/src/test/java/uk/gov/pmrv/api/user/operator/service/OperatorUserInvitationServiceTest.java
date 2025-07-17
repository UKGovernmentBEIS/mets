package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserInvitationServiceTest {

    @InjectMocks
    private OperatorUserInvitationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    
    @Mock
    private UserRoleTypeService userRoleTypeService;

    @Mock
    private OperatorUserRegistrationService operatorUserRegistrationService;

    @Mock
    private ExistingOperatorUserInvitationService existingOperatorUserInvitationService;
    
    @Test
    void inviteUserToAccount_ThrowsExceptionWhenRequesterIsRegulatorAndRoleCodeNotOperatorAdmin() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        AppUser currentUser = createAppUser(currentUserId, RoleTypeConstants.REGULATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);
        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(true);

        BusinessException businessException =
            assertThrows(BusinessException.class,
                () -> service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser));

        assertEquals(ErrorCode.AUTHORITY_USER_REGULATOR_NOT_ALLOWED_TO_ADD_OPERATOR_ROLE_TO_ACCOUNT, businessException.getErrorCode());

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
		verifyNoInteractions(operatorUserAuthService, existingOperatorUserInvitationService,
				operatorUserRegistrationService);
    }

    @Test
    void inviteUserToAccount_WhenUserNotExists() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        AppUser currentUser = createAppUser(currentUserId, RoleTypeConstants.OPERATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);

        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(false);
        when(operatorUserAuthService.getUserIdByEmail(email)).thenReturn(Optional.empty());

        service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
        verify(operatorUserAuthService, times(1)).getUserIdByEmail(email);
        verify(operatorUserRegistrationService, times(1))
            .registerUserToAccount(operatorUserInvitationDTO, accountId, currentUser);
        verifyNoInteractions(existingOperatorUserInvitationService);
    }

    @Test
    void inviteUserToAccount_WhenUserAlreadyExists() {
        String operatorUserRoleCode = "operator";
        String email = "email";
        Long accountId = 1L;
        String currentUserId = "currentUserId";
        String operatorUserId = "operatorUserId";
        AppUser currentUser = createAppUser(currentUserId, RoleTypeConstants.OPERATOR);
        OperatorUserInvitationDTO
            operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, operatorUserRoleCode);

        when(userRoleTypeService.isUserRegulator(currentUser.getUserId())).thenReturn(false);
        when(operatorUserAuthService.getUserIdByEmail(email)).thenReturn(Optional.of(operatorUserId));

        service.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);

        verify(userRoleTypeService, times(1)).isUserRegulator(currentUser.getUserId());
        verify(operatorUserAuthService, times(1)).getUserIdByEmail(email);
        verifyNoInteractions(operatorUserRegistrationService);
        verify(existingOperatorUserInvitationService, times(1))
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, operatorUserId, currentUser);
    }
    
    private AppUser createAppUser(String userId, String roleType) {
        return AppUser.builder().userId(userId).roleType(roleType).build();
    }

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email(email)
            .roleCode(roleCode)
            .build();
    }

}
package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExistingOperatorUserInvitationServiceTest {

    @InjectMocks
    private ExistingOperatorUserInvitationService existingOperatorUserInvitationService;

    @Mock
    private OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    
    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private OperatorAuthorityService operatorAuthorityService;
    
    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Test
    void addExistingUserToAccount() {
        String email = "email";
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(email, roleCode);

        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountName(accountId))
            .thenReturn(accountName);

        existingOperatorUserInvitationService
            .addExistingUserToAccount(operatorUserInvitationDTO, accountId, userId, currentUser);

        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, accountId);
        verify(operatorUserAuthService, times(1)).updateUser(operatorUserInvitationDTO);
        verify(operatorAuthorityService, times(1)).createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(operatorUserInvitationDTO, accountName, authorityUuid);
    }

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String email, String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email(email)
            .roleCode(roleCode)
            .build();
    }

}
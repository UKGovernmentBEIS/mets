package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserActivationServiceTest {

    @InjectMocks
    private OperatorUserActivationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;

    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    
    @Mock
    private OperatorUserRegisterValidationService operatorUserRegisterValidationService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;
    
    @Mock
    private OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    @Test
    void acceptAuthorityAndEnableInvitedUserWithCredentials() {
        String userId = "userId";
        String token = "token";
        String email = "email";
        Long authorityId = 1L;
        Long accountId = 1L;
        OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO = OperatorUserRegistrationWithCredentialsDTO.builder()
            .emailToken(token).build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).id(authorityId).accountId(accountId).build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email(email).build();

        // Mock
        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(token))
            .thenReturn(authority);
        when(operatorUserAuthService.enableAndUpdateUserAndSetPassword(userRegistrationDTO, userId))
            .thenReturn(userDTO);

        // Invoke
        service.acceptAuthorityAndEnableInvitedUserWithCredentials(userRegistrationDTO);

        // Verify
        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(userRegistrationDTO.getEmailToken());
        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, accountId);
        verify(operatorUserAuthService, times(1))
            .enableAndUpdateUserAndSetPassword(userRegistrationDTO, authority.getUserId());
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
        	.acceptAuthorityAndNotify(authorityId);
        verify(operatorUserNotificationGateway, times(1))
            .notifyRegisteredUser(userDTO);
    }

    @Test
    void acceptAuthorityAndEnableInvitedUser() {
        String userId = "userId";
        String token = "token";
        String email = "email";
        Long authorityId = 1L;
        Long accountId = 1L;
        OperatorUserRegistrationDTO userRegistrationDTO = OperatorUserRegistrationDTO.builder().emailToken(token).build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).id(authorityId).accountId(accountId).build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email(email).build();

        when(operatorUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(token))
            .thenReturn(authority);
        when(operatorUserAuthService.enableAndUpdateUser(userRegistrationDTO, userId))
            .thenReturn(userDTO);

        //invoke
        service.acceptAuthorityAndEnableInvitedUser(userRegistrationDTO);

        //verify
        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(userRegistrationDTO.getEmailToken());
        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, accountId);
        verify(operatorUserAuthService, times(1))
            .enableAndUpdateUser(userRegistrationDTO, authority.getUserId());
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
    		.acceptAuthorityAndNotify(authorityId);
    }

    @Test
    void acceptAuthorityAndSetCredentialsToUser() {
    	String userId = "userId";
    	Long authorityId = 1L;
    	Long accountId = 1L;
        InvitedUserCredentialsDTO invitedUserCredentialsDTO = InvitedUserCredentialsDTO.builder()
            .invitationToken("token")
            .password("password")
            .build();
        AuthorityInfoDTO authority = AuthorityInfoDTO.builder().userId(userId).id(authorityId).accountId(accountId).build();
        OperatorUserDTO userDTO = OperatorUserDTO.builder().email("email").build();

        when(operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken()))
            .thenReturn(authority);
        when(operatorUserAuthService
            .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword()))
            .thenReturn(userDTO);

        service.acceptAuthorityAndSetCredentialsToUser(invitedUserCredentialsDTO);

        verify(operatorUserTokenVerificationService, times(1))
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        verify(operatorUserRegisterValidationService, times(1)).validateRegisterForAccount(userId, accountId);
        verify(operatorUserAuthService, times(1))
            .setUserPassword(authority.getUserId(), invitedUserCredentialsDTO.getPassword());
        verify(operatorUserRegisteredAcceptInvitationService, times(1))
    		.acceptAuthorityAndNotify(authorityId);
        verify(operatorUserNotificationGateway, times(1)).notifyRegisteredUser(userDTO);
    }
}
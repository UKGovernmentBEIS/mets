package uk.gov.pmrv.api.user.operator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationResult;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OperatorUserRegistrationServiceTest {
	
    @InjectMocks
    private OperatorUserRegistrationService service;

    @Mock
    private OperatorUserAuthService operatorUserAuthService;
    
    @Mock
    private OperatorAuthorityService operatorAuthorityService;
    
    @Mock
    private OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    
    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private OperatorUserNotificationGateway operatorUserNotificationGateway;
    
    @Test
    void registerUserToAccount() {
        String roleCode = "roleCode";
        String userId = "userId";
        Long accountId = 1L;
        String accountName = "accountName";
        String authorityUuid = "authorityUuid";
        AppUser currentUser = AppUser.builder().userId("current_user_id").build();
        OperatorUserInvitationDTO operatorUserInvitationDTO = createOperatorUserInvitationDTO(roleCode);

		when(operatorUserAuthService.registerOperatorUser(operatorUserInvitationDTO)).thenReturn(userId);
        when(operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser))
            .thenReturn(authorityUuid);
        when(accountQueryService.getAccountName(accountId))
            .thenReturn(accountName);

        service.registerUserToAccount(operatorUserInvitationDTO, accountId, currentUser);

		verify(operatorUserAuthService, times(1)).registerOperatorUser(operatorUserInvitationDTO);
        verify(operatorAuthorityService, times(1))
            .createPendingAuthorityForOperator(accountId, roleCode, userId, currentUser);
        verify(accountQueryService, times(1)).getAccountName(accountId);
        verify(operatorUserNotificationGateway, times(1)).notifyInvitedUser(
            operatorUserInvitationDTO,
            accountName,
            authorityUuid);
    }
    
    @Test
    void registerUser() {
    	String userId = "user";
    	String token = "token";
    	String email = "email";
    	OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO = OperatorUserRegistrationWithCredentialsDTO
            .builder().emailToken(token).build();
    	OperatorUserDTO userDTO = OperatorUserDTO.builder().firstName("fn").lastName("ln").email(email).build();
    	
    	OperatorUserTokenVerificationResult οperatorUserTokenVerificationResult = OperatorUserTokenVerificationResult.builder()
    			.email(email)
    			.status(OperatorUserTokenVerificationStatus.NOT_REGISTERED)
    			.build();
    	
    	when(operatorUserTokenVerificationService.verifyRegistrationTokenAndResolveAndValidateUserExistence(token)).thenReturn(οperatorUserTokenVerificationResult);
    	when(operatorUserAuthService.registerAndEnableOperatorUser(operatorUserRegistrationWithCredentialsDTO, email))
    		.thenReturn(userDTO);
    	when(operatorUserAuthService.getUserIdByEmail(email)).thenReturn(Optional.of(userId));
    	
    	//invoke
    	OperatorUserDTO result = service.registerUser(operatorUserRegistrationWithCredentialsDTO);
    	
    	//assert and verify
    	assertThat(result).isEqualTo(userDTO);
    	
    	verify(operatorUserTokenVerificationService, times(1)).verifyRegistrationTokenAndResolveAndValidateUserExistence(token);
    	verify(operatorUserAuthService, times(1)).registerAndEnableOperatorUser(operatorUserRegistrationWithCredentialsDTO, email);
    	verify(operatorUserAuthService, times(1)).getUserIdByEmail(email);
    	verify(operatorAuthorityService, times(1)).createUserRoleType(userId);
    	verify(operatorUserNotificationGateway, times(1)).notifyRegisteredUser(userDTO);
    }
    
    @Test
	void sendVerificationEmail() {
    	String email = "email";
    	
		// invoke
		service.sendVerificationEmail(email);

		// verify mocks
		verify(operatorUserNotificationGateway, times(1)).notifyEmailVerification(email);
	}

    private OperatorUserInvitationDTO createOperatorUserInvitationDTO(String roleCode) {
        return OperatorUserInvitationDTO.builder()
            .email("email")
            .roleCode(roleCode)
            .firstName("firstName")
            .lastName("lastName")
            .build();
    }

}
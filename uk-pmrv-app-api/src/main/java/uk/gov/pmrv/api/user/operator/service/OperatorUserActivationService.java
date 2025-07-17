package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;

@Service
@RequiredArgsConstructor
public class OperatorUserActivationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;
    private final OperatorUserRegisteredAcceptInvitationService operatorUserRegisteredAcceptInvitationService;

    public OperatorUserDTO acceptAuthorityAndEnableInvitedUserWithCredentials(
        OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        // Get user's authority
        final AuthorityInfoDTO authorityInfo = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(operatorUserRegistrationWithCredentialsDTO.getEmailToken());
        
        // validate
		operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());

        // Activate and update User
        final OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .enableAndUpdateUserAndSetPassword(operatorUserRegistrationWithCredentialsDTO, authorityInfo.getUserId());
        
        // Accept authority and notify
        operatorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authorityInfo.getId());

        // Send notification email
        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        return operatorUserDTO;
    }

    public OperatorUserDTO acceptAuthorityAndEnableInvitedUser(OperatorUserRegistrationDTO operatorUserRegistrationDTO) {
    	// Get user's authority
    	final AuthorityInfoDTO authorityInfo = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(operatorUserRegistrationDTO.getEmailToken());
    	
    	// validate
    	operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());

        // Enable and update User
    	final OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .enableAndUpdateUser(operatorUserRegistrationDTO, authorityInfo.getUserId());
        
        // Accept authority and notify
    	operatorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authorityInfo.getId());
        
        return operatorUserDTO;
    }

    public void acceptAuthorityAndSetCredentialsToUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
    	// Get user's authority
    	final AuthorityInfoDTO authorityInfo = operatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
    	
    	// validate
    	operatorUserRegisterValidationService.validateRegisterForAccount(authorityInfo.getUserId(), authorityInfo.getAccountId());

        // set password
    	final OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .setUserPassword(authorityInfo.getUserId(), invitedUserCredentialsDTO.getPassword());
        
        // Accept authority and notify
    	operatorUserRegisteredAcceptInvitationService.acceptAuthorityAndNotify(authorityInfo.getId());

        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);
    }
}

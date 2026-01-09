package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class VerifierUserActivateService {

    private final UserAuthService userAuthService;
    private final VerifierUserTokenVerificationService verifierUserTokenVerificationService;
    private final VerifierAuthorityService verifierAuthorityService;
    private final VerifierUserRegisterValidationService verifierUserRegisterValidationService;
    private final UserRoleTypeService userRoleTypeService;
    private final VerifierUserNotificationGateway verifierUserNotificationGateway;
    
    /**
	 * Accept authority and activate user (activate: if disabled then enable and set
	 * password. if already enabled then set password)
	 * 
	 * @param invitedUserCredentialsDTO
	 */
    @Transactional
    public void acceptAuthorityAndActivateInvitedUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        final AuthorityInfoDTO authorityInfo = verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        final String userId = authorityInfo.getUserId();
        
        verifierUserRegisterValidationService.validate(authorityInfo.getUserId(),
				authorityInfo.getVerificationBodyId());

        // accept authority
        final Authority authority = verifierAuthorityService.acceptAuthority(authorityInfo.getId());
        
        //create user role type
        userRoleTypeService.createUserRoleTypeOrThrowExceptionIfExists(authorityInfo.getUserId(), RoleTypeConstants.VERIFIER);

        final UserInfoDTO verifierUser = userAuthService.getUserByUserId(userId);
        if(!verifierUser.isEnabled()) {
        	// enable user and set password
        	userAuthService.enableUserAndSetPassword(userId, invitedUserCredentialsDTO.getPassword());	
        } else {
        	// set password
        	userAuthService.setUserPassword(userId, invitedUserCredentialsDTO.getPassword());
        }
        
        // notify
        notify(authority.getUserId(), authority.getCreatedBy());
    }

    @Transactional
    public void acceptAuthorityForRegisteredVerifierInvitedUser(String invitationToken) {
        final AuthorityInfoDTO authorityInfo = verifierUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken);

        // accept authority
        final Authority authority = verifierAuthorityService.acceptAuthority(authorityInfo.getId());
        
        //create user role type
        userRoleTypeService.createUserRoleTypeOrThrowExceptionIfExists(authority.getUserId(), RoleTypeConstants.VERIFIER);

        // notify
        notify(authority.getUserId(), authority.getCreatedBy());
    }
    
    private void notify(String inviteeUserId, String inviterUserId) {
    	final UserInfoDTO invitee = userAuthService.getUserByUserId(inviteeUserId);
        final UserInfoDTO inviter = userAuthService.getUserByUserId(inviterUserId);
    	
		// Notify invitee
        verifierUserNotificationGateway.notifyInviteeAcceptedInvitation(invitee);

        // Notify inviter
        verifierUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);
	}
}

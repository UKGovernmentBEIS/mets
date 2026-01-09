package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.Authority;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.core.service.UserRoleTypeService;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserCredentialsDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;

@Service
@RequiredArgsConstructor
public class RegulatorUserActivateService {

    private final UserAuthService userAuthService;
    private final RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;
    private final RegulatorAuthorityService regulatorAuthorityService;
    private final UserRoleTypeService userRoleTypeService;
    private final RegulatorUserRegisterValidationService regulatorUserRegisterValidationService;
    private final RegulatorUserNotificationGateway regulatorUserNotificationGateway;
    
    /**
	 * Accept authority and activate user (activate: if disabled then enable and set
	 * password. if already enabled then set password)
	 * 
	 * @param invitedUserCredentialsDTO
	 */
    @Transactional
    public void acceptAuthorityAndActivateInvitedUser(InvitedUserCredentialsDTO invitedUserCredentialsDTO) {
        final AuthorityInfoDTO authorityInfo = regulatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitedUserCredentialsDTO.getInvitationToken());
        final Long authorityId = authorityInfo.getId();
        final String userId = authorityInfo.getUserId();
        
        regulatorUserRegisterValidationService.validate(userId,
				authorityInfo.getCompetentAuthority());
        
        // accept authority
        final Authority authority = regulatorAuthorityService.acceptAuthority(authorityId);
        
        //create user role type
        userRoleTypeService.createUserRoleTypeOrThrowExceptionIfExists(userId, RoleTypeConstants.REGULATOR);

        final UserInfoDTO regulatorUser = userAuthService.getUserByUserId(userId);
        if(!regulatorUser.isEnabled()) {
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
    public void acceptAuthorityForRegisteredRegulatorInvitedUser(String invitationToken) {
        final AuthorityInfoDTO authorityInfo = regulatorUserTokenVerificationService
            .verifyInvitationTokenForPendingAuthority(invitationToken);

        final Long authorityId = authorityInfo.getId();

        // accept authority
        final Authority authority = regulatorAuthorityService.acceptAuthority(authorityId);
        
        //create user role type
        userRoleTypeService.createUserRoleTypeOrThrowExceptionIfExists(authority.getUserId(), RoleTypeConstants.REGULATOR);

        // notify
        notify(authority.getUserId(), authority.getCreatedBy());
    }

    private void notify(String inviteeUserId, String inviterUserId) {
		final UserInfoDTO invitee = userAuthService.getUserByUserId(inviteeUserId);
        final UserInfoDTO inviter = userAuthService.getUserByUserId(inviterUserId);

        regulatorUserNotificationGateway.notifyInviteeAcceptedInvitation(invitee);
        regulatorUserNotificationGateway.notifyInviterAcceptedInvitation(invitee, inviter);
	}
}

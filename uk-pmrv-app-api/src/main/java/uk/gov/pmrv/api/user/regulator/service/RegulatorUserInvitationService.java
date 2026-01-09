package uk.gov.pmrv.api.user.regulator.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.netz.api.authorization.regulator.transform.RegulatorPermissionsAdapter;
import uk.gov.netz.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegulatorUserInvitationService {

	private final RegulatorUserAuthService regulatorUserAuthService;
    private final RegulatorAuthorityService regulatorAuthorityService;
    private final RegulatorUserNotificationGateway regulatorUserNotificationGateway;
    private final RegulatorUserRegisterValidationService regulatorUserRegisterValidationService;
    private final RegulatorUserTokenVerificationService regulatorUserTokenVerificationService;
    private final RegulatorUserActivateService regulatorUserActivateService;
    private final UserAuthService userAuthService;
	private final RegulatorPermissionsAdapter regulatorPermissionsAdapter;

	@Transactional
    public void inviteRegulatorUser(RegulatorInvitedUserDTO regulatorInvitedUser, FileDTO signature, AppUser authUser) {
    	//business prevalidations (the keycloak actions do not participate in the current transaction, hence we have to prevalidate before saving in keycloak).
    	Optional<UserInfoDTO> existingUserOpt = regulatorUserAuthService.getUserByEmail(regulatorInvitedUser.getUserDetails().getEmail());
		if (existingUserOpt.isPresent()) {
			regulatorUserRegisterValidationService.validate(existingUserOpt.get().getUserId(),
					authUser.getCompetentAuthority());
		}
		
        //register invited user
        final String userId = regulatorUserAuthService.registerRegulatorInvitedUser(regulatorInvitedUser.getUserDetails(), signature);

        //create authorities for invited user
        String authorityUuid = regulatorAuthorityService.createRegulatorAuthorityPermissions(
            authUser,
            userId,
            authUser.getCompetentAuthority(),
				regulatorPermissionsAdapter.getPermissionsFromPermissionGroupLevels(regulatorInvitedUser.getPermissions()));

        //send invitation email
        regulatorUserNotificationGateway.notifyInvitedUser(regulatorInvitedUser.getUserDetails(), authorityUuid);
    }

    @Transactional
    public InvitedUserInfoDTO acceptInvitation(String invitationToken) {
		final AuthorityInfoDTO authorityInfo = regulatorUserTokenVerificationService
				.verifyInvitationTokenForPendingAuthority(invitationToken);
		final RegulatorUserDTO regulatorUser = regulatorUserAuthService.getRegulatorUserById(authorityInfo.getUserId());
		
		regulatorUserRegisterValidationService.validate(authorityInfo.getUserId(),
				authorityInfo.getCompetentAuthority());
		
		if(BooleanUtils.isTrue(regulatorUser.getEnabled())) {
			if(userAuthService.hasUserPassword(authorityInfo.getUserId())) {
				// accept authority
				regulatorUserActivateService.acceptAuthorityForRegisteredRegulatorInvitedUser(invitationToken);
                return InvitedUserInfoDTO.builder().email(regulatorUser.getEmail())
    					.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED).build();
            } else {
            	return InvitedUserInfoDTO.builder().email(regulatorUser.getEmail())
    					.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY).build();
            }
		} else {
			return InvitedUserInfoDTO.builder().email(regulatorUser.getEmail())
					.invitationStatus(UserInvitationStatus.PENDING_TO_REGISTERED_SET_PASSWORD_ONLY).build();
		}
    }
}

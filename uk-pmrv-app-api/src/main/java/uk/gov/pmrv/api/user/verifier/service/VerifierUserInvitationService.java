package uk.gov.pmrv.api.user.verifier.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.netz.api.authorization.verifier.service.VerifierAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.netz.api.userinfoapi.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.transform.VerifierUserMapper;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifierUserInvitationService {

    private final VerifierUserAuthService verifierUserAuthService;
    private final VerifierAuthorityService verifierAuthorityService;
    private final VerifierUserRegisterValidationService verifierUserRegisterValidationService;
    private final VerifierUserNotificationGateway verifierUserNotificationGateway;
    private final VerifierUserTokenVerificationService verifierUserTokenVerificationService;
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final VerifierUserActivateService verifierUserActivateService;
    private final UserAuthService userAuthService;
    
    private final VerifierUserMapper verifierUserMapper = Mappers.getMapper(VerifierUserMapper.class);

    /**
     *  Invites a new verifier user to join verification body with a specified role.
     * @param appUser the current logged-in {@link AppUser}
     * @param verifierUserInvitation the {@link VerifierUserInvitationDTO}
     */
    @Transactional
    public void inviteVerifierUser(AppUser appUser, VerifierUserInvitationDTO verifierUserInvitation) {
        Long verificationBodyId = appUser.getVerificationBodyId();
        inviteVerifierUser(appUser, verifierUserInvitation, verificationBodyId);
    }

    /**
     * Invites a new verifier user to join verification body with VERIFIER ADMIN role.
     * @param appUser the current logged-in {@link AppUser}
     * @param adminVerifierUserInvitationDTO the {@link AdminVerifierUserInvitationDTO}
     * @param verificationBodyId the id of the verification body to which the user will join
     */
    @Transactional
    public void inviteVerifierAdminUser(AppUser appUser, AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO,
                                        Long verificationBodyId) {
        VerifierUserInvitationDTO verifierUserInvitationDTO =
            verifierUserMapper.toVerifierUserInvitationDTO(adminVerifierUserInvitationDTO);

        // Validate that non disabled verification body exists
        if(!verificationBodyQueryService.existsNonDisabledVerificationBodyById(verificationBodyId)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }

        inviteVerifierUser(appUser, verifierUserInvitationDTO, verificationBodyId);
    }

    @Transactional
    public InvitedUserInfoDTO acceptInvitation(String invitationToken) {
        final AuthorityInfoDTO authorityInfo = verifierUserTokenVerificationService.verifyInvitationTokenForPendingAuthority(invitationToken);

        final VerifierUserDTO verifierUser = verifierUserAuthService.getVerifierUserById(authorityInfo.getUserId());
        
        verifierUserRegisterValidationService.validate(authorityInfo.getUserId(),
				authorityInfo.getVerificationBodyId());
        
        if(BooleanUtils.isTrue(verifierUser.getEnabled())) {
        	if(userAuthService.hasUserPassword(authorityInfo.getUserId())) {
				// accept authority
        		verifierUserActivateService.acceptAuthorityForRegisteredVerifierInvitedUser(invitationToken);
                return InvitedUserInfoDTO.builder().email(verifierUser.getEmail())
    					.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED).build();
            } else {
            	return InvitedUserInfoDTO.builder().email(verifierUser.getEmail())
    					.invitationStatus(UserInvitationStatus.ALREADY_REGISTERED_SET_PASSWORD_ONLY).build();
            }
		} else {
			return InvitedUserInfoDTO.builder().email(verifierUser.getEmail())
					.invitationStatus(UserInvitationStatus.PENDING_TO_REGISTERED_SET_PASSWORD_ONLY).build();
		}
    }

    private void inviteVerifierUser(AppUser appUser, VerifierUserInvitationDTO verifierUserInvitation,
                                    Long verificationBodyId) {
    	//business prevalidations (the keycloak actions do not participate in the current transaction, hence we have to prevalidate before saving in keycloak).
    	Optional<UserInfoDTO> existingUserOpt = verifierUserAuthService.getUserByEmail(verifierUserInvitation.getEmail());
    	if (existingUserOpt.isPresent()) {
    		verifierUserRegisterValidationService.validate(existingUserOpt.get().getUserId(), verificationBodyId);
		}
    	
    	// register/update in keycloak
        String userId = verifierUserAuthService.registerInvitedVerifierUser(verifierUserInvitation);

        // create authorities
        String authorityUuid = verifierAuthorityService.createPendingAuthority(verificationBodyId,
            verifierUserInvitation.getRoleCode(), userId, appUser);

        // notify
        verifierUserNotificationGateway.notifyInvitedUser(verifierUserInvitation, authorityUuid);
    }
}

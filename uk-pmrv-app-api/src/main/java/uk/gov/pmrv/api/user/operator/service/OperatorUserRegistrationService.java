package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserTokenVerificationResult;

@Service
@RequiredArgsConstructor
public class OperatorUserRegistrationService {

    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorAuthorityService operatorAuthorityService;
    private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
    private final AccountQueryService accountQueryService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;

    /**
     * Registers a new user and adds him as OPERATOR to the provided account.
     * @param operatorUserInvitationDTO the {@link OperatorUserInvitationDTO}
     * @param accountId the account id
     * @param currentUser the logged-in {@link AppUser}
     */
    @Transactional
    public void registerUserToAccount(OperatorUserInvitationDTO operatorUserInvitationDTO,
                                                       Long accountId, AppUser currentUser) {
        final String roleCode = operatorUserInvitationDTO.getRoleCode();

        // register in keycloak
		final String userId = operatorUserAuthService.registerOperatorUser(operatorUserInvitationDTO);
		
        // create authority
		final String authorityUuid = operatorAuthorityService.createPendingAuthorityForOperator(accountId, roleCode,
				userId, currentUser);
		
        final String accountName = accountQueryService.getAccountName(accountId);

        // notify
        operatorUserNotificationGateway.notifyInvitedUser(
        		operatorUserInvitationDTO,
        		accountName,
        		authorityUuid);
    }
    
    /**
     * Registers an operator user in Keycloak.
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO} user's under registration
     * @return {@link OperatorUserDTO}
     */
    @Transactional
    public OperatorUserDTO registerUser(OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
		final OperatorUserTokenVerificationResult result = operatorUserTokenVerificationService
				.verifyRegistrationTokenAndResolveAndValidateUserExistence(
						operatorUserRegistrationWithCredentialsDTO.getEmailToken());
		
		// register in keycloak
        final OperatorUserDTO operatorUserDTO = operatorUserAuthService
            .registerAndEnableOperatorUser(operatorUserRegistrationWithCredentialsDTO, result.getEmail());
        
		final String userId = operatorUserAuthService.getUserIdByEmail(result.getEmail())
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_EXIST));
        // create user role type
        operatorAuthorityService.createUserRoleType(userId);

        // notify
        operatorUserNotificationGateway.notifyRegisteredUser(operatorUserDTO);

        return operatorUserDTO;
    }
    
    public void sendVerificationEmail(String email) {
        operatorUserNotificationGateway.notifyEmailVerification(email);
    }
}

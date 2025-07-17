package uk.gov.pmrv.api.user.operator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;

@Log4j2
@Service
@RequiredArgsConstructor
public class ExistingOperatorUserInvitationService {

    private final OperatorUserRegisterValidationService operatorUserRegisterValidationService;
    private final OperatorUserAuthService operatorUserAuthService;
    private final OperatorAuthorityService operatorAuthorityService;
    private final AccountQueryService accountQueryService;
    private final OperatorUserNotificationGateway operatorUserNotificationGateway;

    @Transactional
	public void addExistingUserToAccount(OperatorUserInvitationDTO operatorUserInvitationDTO, Long accountId,
			String userId, AppUser currentUser) {
    	log.debug("Adding existing operator user '{}' to account '{}'", () -> userId, () -> accountId);
    	
    	// validate
    	operatorUserRegisterValidationService.validateRegisterForAccount(userId, accountId);
    	
        //update operator user
        operatorUserAuthService.updateUser(operatorUserInvitationDTO);
        
        //create pending authority
		final String authorityUuid = operatorAuthorityService.createPendingAuthorityForOperator(accountId,
				operatorUserInvitationDTO.getRoleCode(), userId, currentUser);

        final String accountName = accountQueryService.getAccountName(accountId);

        // notify
        operatorUserNotificationGateway.notifyInvitedUser(
        		operatorUserInvitationDTO,
        		accountName,
        		authorityUuid);
    }

}

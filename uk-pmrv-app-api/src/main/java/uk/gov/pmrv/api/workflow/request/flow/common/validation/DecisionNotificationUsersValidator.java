package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import java.util.Set;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class DecisionNotificationUsersValidator {

	private final AccountQueryService accountQueryService;
    private final WorkflowUsersValidator workflowUsersValidator;

    public boolean areUsersValid(final RequestTask requestTask, final DecisionNotification decisionNotification,
            final AppUser appUser) {

        final Long accountId = requestTask.getRequest().getAccountId();
        final Set<String> operators = decisionNotification.getOperators();
        final boolean operatorsValid = workflowUsersValidator.areOperatorsValid(accountId, operators, appUser);
        if (!operatorsValid) {
            return false;
        }

        final Set<Long> externalContacts = decisionNotification.getExternalContacts();
        final boolean externalContactValid = workflowUsersValidator.areExternalContactsValid(externalContacts,
                appUser);
        if (!externalContactValid) {
            return false;
        }

        final String signatory = decisionNotification.getSignatory();
        return workflowUsersValidator.isSignatoryValid(requestTask, signatory);
    }
    
    // validate that aviation accounts should have a contact address
    public boolean shouldHaveContactAddress(final Request request) {
    	return AccountType.INSTALLATION.equals(request.getType().getAccountType()) 
    			|| accountQueryService.getAccountContactAddress(request.getAccountId()) != null;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.rde.validation;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.WorkflowUsersValidator;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdePayload;

@Service
@RequiredArgsConstructor
public class SubmitRdeValidatorService {

    private final WorkflowUsersValidator workflowUsersValidator;

    public void validate(final RequestTask requestTask,
                         final RdePayload rdePayload,
                         final AppUser appUser) {

        final Long accountId = requestTask.getRequest().getAccountId();

        if(rdePayload.getExtensionDate().isBefore(requestTask.getDueDate())
                || rdePayload.getDeadline().isAfter(rdePayload.getExtensionDate())
                ||!workflowUsersValidator.areOperatorsValid(accountId, rdePayload.getOperators(), appUser)
                || !workflowUsersValidator.isSignatoryValid(requestTask, rdePayload.getSignatory())){
            throw new BusinessException(ErrorCode.FORM_VALIDATION);
        }
    }
}

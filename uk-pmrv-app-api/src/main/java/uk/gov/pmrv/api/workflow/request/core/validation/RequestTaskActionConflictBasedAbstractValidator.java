package uk.gov.pmrv.api.workflow.request.core.validation;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Optional;
import java.util.Set;

public abstract class RequestTaskActionConflictBasedAbstractValidator implements RequestTaskActionValidator{

    protected abstract RequestTaskActionValidationResult.ErrorMessage getErrorMessage();

    protected abstract Set<RequestTaskType> getConflictingRequestTaskTypes();

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {

        final Set<RequestTaskType> requestTaskTypes = this.getConflictingRequestTaskTypes();

        Optional<RequestTask> conflictingRequestTask = requestTask.getRequest().getRequestTasks().stream()
            .filter(r -> requestTaskTypes.contains(r.getType()))
            .findFirst();

        return conflictingRequestTask.isEmpty()
            ? RequestTaskActionValidationResult.validResult()
            : RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
    }
}

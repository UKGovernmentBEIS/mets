package uk.gov.pmrv.api.workflow.request.core.validation;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Set;

@Service
public class VerificationBodyExistenceRequestTaskActionValidator extends
    RequestTaskActionConflictBasedAbstractValidator {

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.NO_VB_FOUND;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(
            RequestTaskActionType.AER_REQUEST_VERIFICATION,
            RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION);
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        return ObjectUtils.isEmpty(requestTask.getRequest().getVerificationBodyId())
            ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
            : RequestTaskActionValidationResult.validResult();
    }
}

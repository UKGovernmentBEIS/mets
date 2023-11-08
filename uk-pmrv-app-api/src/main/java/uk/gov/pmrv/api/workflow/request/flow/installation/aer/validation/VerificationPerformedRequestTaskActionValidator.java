package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
public class VerificationPerformedRequestTaskActionValidator extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.AER_SUBMIT_APPLICATION, RequestTaskActionType.AER_SUBMIT_APPLICATION_AMEND);
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        final AerApplicationSubmitRequestTaskPayload taskPayload =
            (AerApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        return taskPayload.getPermitType().equals(PermitType.GHGE) && !taskPayload.isVerificationPerformed()
            ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
            : RequestTaskActionValidationResult.validResult();
    }
}

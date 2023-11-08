package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.validation;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerApplicationRequestTaskPayload;

import java.util.Set;

public abstract class AviationAerVerificationNotEligibleRequestTaskActionValidator
        extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    public RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.VERIFICATION_NOT_ELIGIBLE;
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        AviationAerApplicationRequestTaskPayload aviationAerUkEtsApplicationSubmitRequestTaskPayload =
                (AviationAerApplicationRequestTaskPayload) requestTask.getPayload();

        return Boolean.FALSE.equals(aviationAerUkEtsApplicationSubmitRequestTaskPayload.getReportingRequired())
                ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
                : RequestTaskActionValidationResult.validResult();
    }
}

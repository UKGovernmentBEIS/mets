package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.submit.domain.AviationAerCorsiaApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
public class AviationAerCorsiaVerificationAlreadyPerformedRequestTaskActionValidator
        extends RequestTaskActionConflictBasedAbstractValidator {
    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND;
    }

    @Override
    protected Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_VERIFICATION,
            RequestTaskActionType.AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION
        );
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        AviationAerCorsiaApplicationSubmitRequestTaskPayload aviationAerCorsiaApplicationSubmitRequestTaskPayload =
                (AviationAerCorsiaApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        if (Boolean.TRUE.equals(aviationAerCorsiaApplicationSubmitRequestTaskPayload.getReportingRequired())
                && aviationAerCorsiaApplicationSubmitRequestTaskPayload.isVerificationPerformed()) {
            return RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
        }
        return RequestTaskActionValidationResult.validResult();
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestPayload;

import java.util.Set;

@Service
public class ALRSubmitToRegulatorVerificationPerformedValidationService extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        final ALRApplicationSubmitRequestTaskPayload taskPayload =
                (ALRApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        ALRRequestPayload requestPayload = (ALRRequestPayload) request.getPayload();

        return !taskPayload.isVerificationPerformed() || ObjectUtils.isEmpty(requestPayload.getVerificationReport())
                ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
                : RequestTaskActionValidationResult.validResult();
    }


    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.ALR_SUBMIT_TO_REGULATOR);
    }

    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.NO_VERIFICATION_PERFORMED;
    }

    @Override
    protected Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }
}

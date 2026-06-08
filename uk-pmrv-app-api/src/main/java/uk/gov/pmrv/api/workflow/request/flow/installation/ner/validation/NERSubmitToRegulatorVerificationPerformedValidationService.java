package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerRequestPayload;

import java.util.Set;

@Service
public class NERSubmitToRegulatorVerificationPerformedValidationService extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        final NerApplicationSubmitRequestTaskPayload taskPayload =
                (NerApplicationSubmitRequestTaskPayload) requestTask.getPayload();
        Request request = requestTask.getRequest();
        NerRequestPayload requestPayload = (NerRequestPayload) request.getPayload();

        return !taskPayload.isVerificationPerformed() || ObjectUtils.isEmpty(requestPayload.getVerificationReport())
                ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
                : RequestTaskActionValidationResult.validResult();
    }


    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.NER_SUBMIT_APPLICATION);
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

package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.validation;


import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;

import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
public class BDRSubmitToRegulatorVerificationPerformedValidationService extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        final BDRApplicationSubmitRequestTaskPayload taskPayload =
            (BDRApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        return taskPayload.getBdr().getIsApplicationForFreeAllocation() && !taskPayload.isVerificationPerformed()
            ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
            : RequestTaskActionValidationResult.validResult();
    }


    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.BDR_SUBMIT_TO_REGULATOR);
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

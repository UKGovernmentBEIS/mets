package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;
import uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.domain.BDRS2ApplicationSubmitRequestTaskPayload;

import java.util.Set;

@Service
public class BDRS2SubmitToRegulatorVerificationPerformedValidationService extends RequestTaskActionConflictBasedAbstractValidator {

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        final BDRS2ApplicationSubmitRequestTaskPayload taskPayload =
                (BDRS2ApplicationSubmitRequestTaskPayload) requestTask.getPayload();

        //if CBAM flag is true, verification is required
        boolean CBAMflag = Boolean.TRUE.equals(taskPayload.getBdrs2().getBdrs2guardQuestions().getRequiresAdditionalSubInstallationSplitsForCbam());

        return  CBAMflag && !taskPayload.isVerificationPerformed()
                ? RequestTaskActionValidationResult.invalidResult(this.getErrorMessage())
                : RequestTaskActionValidationResult.validResult();
    }


    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.BDRS2_SUBMIT_TO_REGULATOR);
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

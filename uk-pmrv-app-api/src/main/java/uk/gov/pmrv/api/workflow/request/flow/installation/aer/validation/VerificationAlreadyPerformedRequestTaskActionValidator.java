package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionValidationResult;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayloadVerifiable;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.validation.RequestTaskActionConflictBasedAbstractValidator;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class VerificationAlreadyPerformedRequestTaskActionValidator extends
        RequestTaskActionConflictBasedAbstractValidator {
    private final InstallationAccountQueryService installationAccountQueryService;


    @Override
    protected RequestTaskActionValidationResult.ErrorMessage getErrorMessage() {
        return RequestTaskActionValidationResult.ErrorMessage.VERIFIED_DATA_FOUND;
    }

    @Override
    public Set<RequestTaskActionType> getTypes() {
        return Set.of(RequestTaskActionType.AER_REQUEST_VERIFICATION,
            RequestTaskActionType.AER_REQUEST_AMENDS_VERIFICATION);
    }

    @Override
    public Set<RequestTaskType> getConflictingRequestTaskTypes() {
        return Set.of();
    }

    @Override
    public RequestTaskActionValidationResult validate(final RequestTask requestTask) {
        EmitterType emitterType = installationAccountQueryService
                .getInstallationAccountInfoDTOById(requestTask.getRequest().getAccountId()).getEmitterType();

        if(emitterType.equals(EmitterType.GHGE) && ((RequestTaskPayloadVerifiable)requestTask.getPayload()).isVerificationPerformed()) {
            return RequestTaskActionValidationResult.invalidResult(this.getErrorMessage());
        }
        return RequestTaskActionValidationResult.validResult();
    }
}

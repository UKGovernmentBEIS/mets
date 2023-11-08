package uk.gov.pmrv.api.workflow.request.flow.common.service;


import java.util.Set;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@RequiredArgsConstructor
public abstract class RequestCreateAccountRelatedValidator implements RequestCreateByAccountValidator {

    private final RequestCreateValidatorService requestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId) {
        return requestCreateValidatorService
                .validate(accountId, this.getApplicableAccountStatuses(), this.getMutuallyExclusiveRequests());
    }

    protected abstract Set<AccountStatus> getApplicableAccountStatuses();

    protected abstract Set<RequestType> getMutuallyExclusiveRequests();
}

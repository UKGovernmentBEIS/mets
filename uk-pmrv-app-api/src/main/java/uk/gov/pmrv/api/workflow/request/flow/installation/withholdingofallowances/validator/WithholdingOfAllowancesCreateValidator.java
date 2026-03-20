package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class WithholdingOfAllowancesCreateValidator extends RequestCreateAccountRelatedValidator {

    private final RequestCreateValidatorService requestCreateValidatorService;

    public WithholdingOfAllowancesCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
        this.requestCreateValidatorService = requestCreateValidatorService;
    }

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestCreateAccountStatusValidationResult accountStatusResult =
                requestCreateValidatorService.validateAccountStatuses(accountId, this.getApplicableAccountStatuses());
        if (!accountStatusResult.isValid()) {
            validationResult.setValid(false);
            validationResult.setApplicableAccountStatuses(this.getApplicableAccountStatuses());
            validationResult.setReportedAccountStatus(accountStatusResult.getReportedAccountStatus());
        }

        RequestCreateRequestTypeValidationResult conflictingRequestsResult =
                requestCreateValidatorService.validateInProgressAndCompletedConflictingRequestTypes(
                        accountId, this.getMutuallyExclusiveRequests(),getMutuallyExclusiveRequestStatuses());
        if (!conflictingRequestsResult.isValid()) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(conflictingRequestsResult.getReportedRequestTypes());
        }

        return validationResult;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES;
    }

    @Override
    public Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(InstallationAccountStatus.NEW, InstallationAccountStatus.LIVE,
            InstallationAccountStatus.AWAITING_REVOCATION, InstallationAccountStatus.AWAITING_SURRENDER,
            InstallationAccountStatus.AWAITING_TRANSFER);
    }

    @Override
    public Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.WITHHOLDING_OF_ALLOWANCES);
    }

    public Set<RequestStatus> getMutuallyExclusiveRequestStatuses() {
        return Set.of(RequestStatus.IN_PROGRESS, RequestStatus.COMPLETED);
    }

}

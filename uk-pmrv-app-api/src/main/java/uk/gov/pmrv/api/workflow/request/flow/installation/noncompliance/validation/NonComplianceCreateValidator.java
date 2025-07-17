package uk.gov.pmrv.api.workflow.request.flow.installation.noncompliance.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class NonComplianceCreateValidator extends RequestCreateAccountRelatedValidator {

    public NonComplianceCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
            InstallationAccountStatus.NEW,
            InstallationAccountStatus.LIVE,
            InstallationAccountStatus.AWAITING_SURRENDER,
            InstallationAccountStatus.AWAITING_REVOCATION
        );
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.NON_COMPLIANCE;
    }
}

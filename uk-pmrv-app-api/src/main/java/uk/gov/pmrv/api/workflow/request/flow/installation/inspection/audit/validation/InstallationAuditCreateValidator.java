package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;


@Service
public class InstallationAuditCreateValidator  extends RequestCreateAccountRelatedValidator {
    public InstallationAuditCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.INSTALLATION_AUDIT;
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(InstallationAccountStatus.LIVE);
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of();
    }
}

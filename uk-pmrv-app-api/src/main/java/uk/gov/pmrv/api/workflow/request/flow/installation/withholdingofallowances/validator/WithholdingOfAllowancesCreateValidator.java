package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Collections;
import java.util.Set;

@Service
public class WithholdingOfAllowancesCreateValidator extends RequestCreateAccountRelatedValidator {

    public WithholdingOfAllowancesCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
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
        return Collections.emptySet();
    }

}

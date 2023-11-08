package uk.gov.pmrv.api.workflow.request.flow.aviation.noncompliance.validation;

import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class AviationNonComplianceCreateValidator extends RequestCreateAccountRelatedValidator {

    public AviationNonComplianceCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
            AviationAccountStatus.NEW,
            AviationAccountStatus.LIVE
        );
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AVIATION_NON_COMPLIANCE;
    }
}

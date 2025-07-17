package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class PermitNotificationCreateValidator extends RequestCreateAccountRelatedValidator {

    public PermitNotificationCreateValidator(final RequestCreateValidatorService requestCreateValidatorService) {
        super(requestCreateValidatorService);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
                InstallationAccountStatus.LIVE,
                InstallationAccountStatus.AWAITING_SURRENDER,
                InstallationAccountStatus.SURRENDERED,
                InstallationAccountStatus.AWAITING_REVOCATION,
                InstallationAccountStatus.REVOKED,
                InstallationAccountStatus.AWAITING_TRANSFER,
                InstallationAccountStatus.TRANSFERRED);
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return  Set.of();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.PERMIT_NOTIFICATION;
    }
}

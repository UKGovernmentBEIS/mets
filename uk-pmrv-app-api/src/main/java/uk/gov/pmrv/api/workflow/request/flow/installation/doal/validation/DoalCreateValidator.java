package uk.gov.pmrv.api.workflow.request.flow.installation.doal.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class DoalCreateValidator extends RequestCreateAccountRelatedValidator {

    private final InstallationAccountQueryService installationAccountQueryService;

    public DoalCreateValidator(final RequestCreateValidatorService requestCreateValidatorService,
                              final InstallationAccountQueryService installationAccountQueryService) {
        super(requestCreateValidatorService);
        this.installationAccountQueryService = installationAccountQueryService;
    }

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
        final InstallationAccountDTO accountDTOById = installationAccountQueryService.getAccountDTOById(accountId);
        if (accountDTOById.getEmitterType() == EmitterType.HSE) {
            return RequestCreateValidationResult.builder().isAvailable(false).build();
        }

        return super.validateAction(accountId);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
                InstallationAccountStatus.LIVE,
                InstallationAccountStatus.AWAITING_REVOCATION,
                InstallationAccountStatus.AWAITING_SURRENDER
        );
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.DOAL);
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.DOAL;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAccountRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.List;
import java.util.Set;

@Service
public class NerCreateValidator extends RequestCreateAccountRelatedValidator {

    private final RequestRepository requestRepository;
    private final InstallationAccountQueryService installationAccountQueryService;
    
    public NerCreateValidator(final RequestCreateValidatorService requestCreateValidatorService, RequestRepository requestRepository,
                              final InstallationAccountQueryService installationAccountQueryService) {
        
        super(requestCreateValidatorService);
        this.requestRepository = requestRepository;
        this.installationAccountQueryService = installationAccountQueryService;
    }

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId) {

        // this is safe to call, as RequestType.NER is tied to AccountType.INSTALLATION
        final InstallationAccountDTO accountDTOById = installationAccountQueryService.getAccountDTOById(accountId);
        List<Request> BDRrequestsList = requestRepository.findByAccountIdAndType(accountId, RequestType.BDR);
        List<Request> completedNERrequestsList = requestRepository.findByAccountIdAndTypeInAndStatusIn(accountId, Set.of(RequestType.NER), Set.of(RequestStatus.COMPLETED));

        RequestCreateValidationResult result = RequestCreateValidationResult.builder().build();

        boolean isNERAvailable = accountDTOById.getEmitterType() == EmitterType.GHGE
                && accountDTOById.getFaStatus() == false
                && BDRrequestsList.isEmpty();

        if (!isNERAvailable) {
            result.setAvailable(false);
            return result;
        }

        if (!completedNERrequestsList.isEmpty()) {
            result.setValid(false);
            return result;
        }


        return super.validateAction(accountId);
    }
    
    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
            InstallationAccountStatus.LIVE
        );
    }

    @Override
    protected Set<RequestType> getMutuallyExclusiveRequests() {
        return Set.of(RequestType.NER);
    }
    
    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.NER;
    }
}

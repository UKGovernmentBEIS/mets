package uk.gov.pmrv.api.workflow.request.flow.installation.hseti.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIAllocationPeriod;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.installation.hseti.domain.HSETIRequestPayload;

import java.util.Arrays;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HSETICreateValidator extends RequestCreateAccountRelatedValidator {

    private final RequestRepository requestRepository;
    private final InstallationAccountQueryService installationAccountQueryService;

    public HSETICreateValidator(final RequestCreateValidatorService requestCreateValidatorService, RequestRepository requestRepository, InstallationAccountQueryService installationAccountQueryService) {
        super(requestCreateValidatorService);
        this.requestRepository = requestRepository;
        this.installationAccountQueryService = installationAccountQueryService;
    }

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId) {

        final InstallationAccountDTO accountDTOById = installationAccountQueryService.getAccountDTOById(accountId);

        if (!EmitterType.HSE.equals(accountDTOById.getEmitterType())) {
            return RequestCreateValidationResult.builder().available(false).build();
        }

        List<Request> requestList = requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.HSE_TI, RequestStatus.IN_PROGRESS);
        Set<HSETIAllocationPeriod> alreadyUsedAllocationPeriods =
                requestList.stream().map(request -> {
                    HSETIRequestMetadata metadata = (HSETIRequestMetadata) request.getMetadata();
                    return metadata.getAllocationPeriod();
                }).collect(Collectors.toSet());

        if (Arrays.stream(HSETIAllocationPeriod.values()).allMatch(alreadyUsedAllocationPeriods::contains)) {
            return RequestCreateValidationResult.builder().valid(false).build();
        }

        return super.validateAction(accountId);
    }

    public RequestCreateValidationResult validateAction(final HSETIRequestCreateActionPayload payload, final Long accountId) {
        List<Request> requestList = requestRepository.findByAccountIdAndTypeAndStatus(accountId, RequestType.HSE_TI, RequestStatus.IN_PROGRESS);
        HSETIAllocationPeriod newAllocationPeriod = payload.getAllocationPeriod();

        if (newAllocationPeriod != null) {
            for (Request request : requestList) {

                HSETIRequestPayload requestPayload = (HSETIRequestPayload) request.getPayload();
                HSETIAllocationPeriod existingHSETIAllocationPeriod =  requestPayload.getHseti().getAllocationPeriod();

                if (newAllocationPeriod.equals(existingHSETIAllocationPeriod)) {
                    return RequestCreateValidationResult.builder().valid(false).build();
                }
            }
        }


        return RequestCreateValidationResult.builder().valid(true).build();
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.HSE_TI;
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
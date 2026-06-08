package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NERRequestCreateActionPayload;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NERReinitiateValidator implements RequestCreateByRequestValidator<NERRequestCreateActionPayload> {

    private final RequestRepository requestRepository;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final RequestQueryService requestQueryService;
    private final RequestCreateValidatorService requestCreateValidatorService;


    @Override
    public RequestCreateValidationResult validateAction(final Long accountId, NERRequestCreateActionPayload payload) {
        final RequestCreateValidationResult overallValidationResult =
                RequestCreateValidationResult.builder().valid(true).build();

        RequestDetailsDTO requestDetailsDTO = requestQueryService.findRequestDetailsById(payload.getRequestId());
        if(!requestDetailsDTO.getRequestType().equals(RequestType.NER)) {
            throw new BusinessException(MetsErrorCode.NER_REQUEST_IS_NOT_NER, payload.getRequestId());
        }

        RequestCreateRequestTypeValidationResult requestTypeValidationResult =
                this.validateRequestType(requestDetailsDTO);

        if(!requestTypeValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedRequestTypes(requestTypeValidationResult.getReportedRequestTypes());
        }


        RequestCreateAccountStatusValidationResult accountStatusValidationResult = requestCreateValidatorService
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));


        if(!accountStatusValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedAccountStatus(accountStatusValidationResult.getReportedAccountStatus());
            overallValidationResult.setApplicableAccountStatuses(Set.of(InstallationAccountStatus.LIVE));
        }

        final InstallationAccountDTO accountDTOById = installationAccountQueryService.getAccountDTOById(accountId);
        List<Request> BDRrequestsList = requestRepository.findByAccountIdAndType(accountId, RequestType.BDR);

        boolean isNERAvailable = accountDTOById.getEmitterType() == EmitterType.GHGE
                && accountDTOById.getFaStatus() == false
                && BDRrequestsList.isEmpty();

        if (!isNERAvailable) {
            overallValidationResult.setAvailable(false);
        }

        return overallValidationResult;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.NER_RE_INITIATE;
    }

    private RequestCreateRequestTypeValidationResult validateRequestType(RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        if (!requestDetailsDTO.getRequestStatus().equals(RequestStatus.COMPLETED)) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.NER));
        }
        return result;
    }
}

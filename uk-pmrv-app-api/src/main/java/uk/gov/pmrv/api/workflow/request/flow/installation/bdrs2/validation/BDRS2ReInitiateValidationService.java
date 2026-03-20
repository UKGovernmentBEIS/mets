package uk.gov.pmrv.api.workflow.request.flow.installation.bdrs2.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRS2ReInitiateValidationService implements RequestCreateByRequestValidator<ReportRelatedRequestCreateActionPayload> {

    private final RequestQueryService requestQueryService;
    private final RequestCreateValidatorService requestCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId, ReportRelatedRequestCreateActionPayload payload) {
        RequestDetailsDTO requestDetailsDTO = requestQueryService.findRequestDetailsById(payload.getRequestId());

        if(!requestDetailsDTO.getRequestType().equals(RequestType.BDRS2)) {
            throw new BusinessException(MetsErrorCode.BDRS2_REQUEST_IS_NOT_BDRS2, payload.getRequestId());
        }

        final RequestCreateValidationResult overallValidationResult =
                RequestCreateValidationResult.builder().valid(true).build();


        RequestCreateAccountStatusValidationResult accountStatusValidationResult = requestCreateValidatorService
                .validateAccountStatuses(accountId, Set.of(InstallationAccountStatus.LIVE));


        if(!accountStatusValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedAccountStatus(accountStatusValidationResult.getReportedAccountStatus());
            overallValidationResult.setApplicableAccountStatuses(Set.of(InstallationAccountStatus.LIVE));
        }

        RequestCreateRequestTypeValidationResult requestTypeValidationResult =
                this.validateRequestType(requestDetailsDTO);

        if(!requestTypeValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedRequestTypes(requestTypeValidationResult.getReportedRequestTypes());
        }

        return overallValidationResult;
    }


    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.BDRS2;
    }


    private RequestCreateRequestTypeValidationResult validateRequestType(RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        if (!requestDetailsDTO.getRequestStatus().equals(RequestStatus.COMPLETED)) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.BDRS2));
        }
        return result;
    }
}

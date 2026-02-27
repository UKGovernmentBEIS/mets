package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateByRequestValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesReCreateActionPayload;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class WithholdingOfAllowancesReInitiateValidationService implements RequestCreateByRequestValidator<WithholdingOfAllowancesReCreateActionPayload> {

    private final RequestQueryService requestQueryService;
    private final RequestCreateValidatorService requestCreateValidatorService;
    private final WithholdingOfAllowancesCreateValidator withholdingOfAllowancesCreateValidator;

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId, WithholdingOfAllowancesReCreateActionPayload payload) {
        RequestDetailsDTO requestDetailsDTO = requestQueryService.findRequestDetailsById(payload.getRequestId());

        if (!requestDetailsDTO.getRequestType().equals(RequestType.WITHHOLDING_OF_ALLOWANCES)) {
            throw new BusinessException(MetsErrorCode.WITHHOLDING_OF_ALLOWANCES_CREATION_NOT_ALLOWED, payload.getRequestId());
        }

        final RequestCreateValidationResult overallValidationResult =
                RequestCreateValidationResult.builder().valid(true).build();

        RequestCreateAccountStatusValidationResult accountStatusValidationResult = requestCreateValidatorService
                .validateAccountStatuses(accountId, withholdingOfAllowancesCreateValidator.getApplicableAccountStatuses());

        if (!accountStatusValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedAccountStatus(accountStatusValidationResult.getReportedAccountStatus());
            overallValidationResult.setApplicableAccountStatuses(withholdingOfAllowancesCreateValidator.getApplicableAccountStatuses());
        }

        RequestCreateRequestTypeValidationResult requestTypeValidationResult =
                this.validateRequestType(requestDetailsDTO);

        if (!requestTypeValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedRequestTypes(requestTypeValidationResult.getReportedRequestTypes());
        }

        return overallValidationResult;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.WITHHOLDING_OF_ALLOWANCES_RE_INITIATE;
    }

    private RequestCreateRequestTypeValidationResult validateRequestType(RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        if (!requestDetailsDTO.getRequestStatus().equals(RequestStatus.COMPLETED)) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.WITHHOLDING_OF_ALLOWANCES));

        }
        return result;
    }
}

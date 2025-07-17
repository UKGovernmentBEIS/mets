package uk.gov.pmrv.api.workflow.request.flow.aviation.doe.corsia.validation;


import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAerRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.util.Set;

@Service
public class AviationDoECorsiaInitiateValidatorService  extends RequestCreateAerRelatedValidator {

    public AviationDoECorsiaInitiateValidatorService(RequestCreateValidatorService requestCreateValidatorService,
                                              RequestQueryService requestQueryService) {
        super(requestCreateValidatorService, requestQueryService);
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
                AviationAccountStatus.NEW,
                AviationAccountStatus.LIVE);
    }

    @Override
    protected RequestType getReferableRequestType() {
        return RequestType.AVIATION_AER_CORSIA;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AVIATION_DOE_CORSIA;
    }

    @Override
    protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) requestDetailsDTO.getRequestMetadata();
        if(metadata == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestDetailsDTO.getId());
        }

        if (requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.AVIATION_DOE_CORSIA,
                RequestStatus.IN_PROGRESS, accountId, metadata.getYear())) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.AVIATION_DOE_CORSIA));
        }

        return result;
    }
}

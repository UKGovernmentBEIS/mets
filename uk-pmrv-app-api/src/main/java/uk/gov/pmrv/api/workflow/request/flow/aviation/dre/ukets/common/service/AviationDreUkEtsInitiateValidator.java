package uk.gov.pmrv.api.workflow.request.flow.aviation.dre.ukets.common.service;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatusType;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.aviation.service.reportingstatus.AviationAccountReportingStatusService;
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

import java.util.Optional;
import java.util.Set;

@Service
public class AviationDreUkEtsInitiateValidator extends RequestCreateAerRelatedValidator {

    private final AviationAccountReportingStatusService aviationAccountReportingStatusService;

    public AviationDreUkEtsInitiateValidator(RequestCreateValidatorService requestCreateValidatorService,
                                             RequestQueryService requestQueryService,
                                             AviationAccountReportingStatusService aviationAccountReportingStatusService) {
        super(requestCreateValidatorService, requestQueryService);
        this.aviationAccountReportingStatusService = aviationAccountReportingStatusService;
    }

    @Override
    protected Set<AccountStatus> getApplicableAccountStatuses() {
        return Set.of(
                AviationAccountStatus.NEW,
                AviationAccountStatus.LIVE);
    }

    @Override
    protected RequestType getReferableRequestType() {
        return RequestType.AVIATION_AER_UKETS;
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AVIATION_DRE_UKETS;
    }

    @Override
    protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        AviationAerRequestMetadata metadata = (AviationAerRequestMetadata) requestDetailsDTO.getRequestMetadata();
        if(metadata == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, requestDetailsDTO.getId());
        }

        Optional<AviationAccountReportingStatus> accountReportingStatusOptional =
                aviationAccountReportingStatusService.getReportingStatusByYear(accountId, metadata.getYear());

        boolean requestAlreadyExists =
                requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(RequestType.AVIATION_DRE_UKETS,
                        RequestStatus.IN_PROGRESS, accountId, metadata.getYear());

        if(requestAlreadyExists || accountReportingStatusOptional.isEmpty() || !AviationAccountReportingStatusType.REQUIRED_TO_REPORT
                .equals(accountReportingStatusOptional.get().getStatus())) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.AVIATION_DRE_UKETS));
        }
        return result;
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.annualoffsetting.validation;

import java.time.Year;
import java.util.Set;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAerRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class AviationCorsiaAerAnnualOffsettingReInitiateValidator extends RequestCreateAerRelatedValidator {

    private static final Year ANNUAL_OFFSETTING_BASE_YEAR = Year.of(2023);

    public AviationCorsiaAerAnnualOffsettingReInitiateValidator(final RequestCreateValidatorService requestCreateValidatorService,
                                                                final RequestQueryService requestQueryService) {
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
        return RequestCreateActionType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING;
    }

	@Override
	protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
		final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        AviationAerCorsiaRequestMetadata requestMetadata = (AviationAerCorsiaRequestMetadata)requestDetailsDTO.getRequestMetadata();

        boolean existsAnnualOffsettingInProgress = false;
        if(requestMetadata != null) {
            if(requestMetadata.getYear().isBefore(ANNUAL_OFFSETTING_BASE_YEAR)) {
                setValidatorResults(result);
                return result;
            }

            existsAnnualOffsettingInProgress = requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                    RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING, RequestStatus.IN_PROGRESS,
                    accountId, requestMetadata.getYear());

        }

        if (existsAnnualOffsettingInProgress) {
            setValidatorResults(result);
		}
		return result;
	}

    private void setValidatorResults(RequestCreateRequestTypeValidationResult result){
        result.setValid(false);
        result.setReportedRequestTypes(Set.of(RequestType.AVIATION_AER_CORSIA_ANNUAL_OFFSETTING));
    }
}

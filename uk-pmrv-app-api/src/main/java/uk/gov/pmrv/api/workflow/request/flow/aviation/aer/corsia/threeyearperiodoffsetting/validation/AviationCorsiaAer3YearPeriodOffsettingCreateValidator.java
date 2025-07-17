package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.threeyearperiodoffsetting.validation;

import java.time.Year;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.corsia.common.domain.AviationAerCorsiaRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReportRelatedRequestCreateActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateAccountStatusValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateRequestTypeValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateAerRelatedValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

@Service
public class AviationCorsiaAer3YearPeriodOffsettingCreateValidator extends RequestCreateAerRelatedValidator {

    private static final String AER_CORSIA_3YEAR_FIRST_REPORTING_YEAR_CONFIGURATION_KEY =
            "aer.corsia.3year.first-reporting-year";
    private final ConfigurationService configurationService;


    public AviationCorsiaAer3YearPeriodOffsettingCreateValidator(RequestCreateValidatorService requestCreateValidatorService, RequestQueryService requestQueryService, ConfigurationService configurationService) {
        super(requestCreateValidatorService, requestQueryService);
        this.configurationService = configurationService;
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
        return RequestCreateActionType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING;
    }

    @Override
    protected RequestCreateRequestTypeValidationResult validateRequestType(Long accountId, RequestDetailsDTO requestDetailsDTO) {
        final RequestCreateRequestTypeValidationResult result = RequestCreateRequestTypeValidationResult.builder().valid(true).build();
        AviationAerCorsiaRequestMetadata requestMetadata = (AviationAerCorsiaRequestMetadata)requestDetailsDTO.getRequestMetadata();

        Year aerYear = requestMetadata.getYear();

        boolean existsAnnualOffsettingInProgress = requestQueryService.existByRequestTypeAndStatusAndAccountIdAndMetadataYear(
                RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING, RequestStatus.IN_PROGRESS,
                accountId, requestMetadata.getYear());

        if (!isValidYear(aerYear)) {
            result.setValid(false);
        }

        if (existsAnnualOffsettingInProgress) {
            result.setValid(false);
            result.setReportedRequestTypes(Set.of(RequestType.AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING));
        }
        return result;
    }

    @Override
    public RequestCreateValidationResult validateAction(final Long accountId, ReportRelatedRequestCreateActionPayload payload) {

        RequestDetailsDTO requestDetailsDTO = requestQueryService.findRequestDetailsById(payload.getRequestId());

        if (!Objects.equals(requestDetailsDTO.getRequestType(), this.getReferableRequestType())) {
            throw new BusinessException(MetsErrorCode.INVALID_AVIATION_AER, payload.getRequestId());
        }

        final RequestCreateValidationResult overallValidationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestCreateAccountStatusValidationResult accountStatusValidationResult = requestCreateValidatorService
                .validateAccountStatuses(accountId, this.getApplicableAccountStatuses());

        if (!accountStatusValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedAccountStatus(accountStatusValidationResult.getReportedAccountStatus());
            overallValidationResult.setApplicableAccountStatuses(this.getApplicableAccountStatuses());
        }

        RequestCreateRequestTypeValidationResult requestTypeValidationResult = this.validateRequestType(accountId, requestDetailsDTO);
        if (!requestTypeValidationResult.isValid()) {
            overallValidationResult.setValid(false);
            overallValidationResult.setReportedRequestTypes(requestTypeValidationResult.getReportedRequestTypes());
        }

        return overallValidationResult;
    }

    public boolean isValidYear(Year aerYear) {

        int year = aerYear.getValue();

        int firstReportingYear = configurationService.getConfigurationByKey(AER_CORSIA_3YEAR_FIRST_REPORTING_YEAR_CONFIGURATION_KEY)
                .map(ConfigurationDTO::getValue).filter(Integer.class::isInstance).map(Integer.class::cast)
                .orElse(2023);

        // Check if the year is part of the sequence starting from firstReportingYear (obtained from db)  with increments of 3
        return (year >= firstReportingYear) && ((year - firstReportingYear) % 3 == 0);
    }
}

package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountInfoDTO;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountReportingStatus;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AviationAerCreationService {

    private final List<AviationAerCreationRequestParamsBuilderService> aviationAerCreationRequestParamsBuilderServices;
    private final AviationAccountQueryService aviationAccountQueryService;
    private final AviationAerCreationValidatorService aviationAerCreationValidatorService;
    private final StartProcessRequestService startProcessRequestService;
    private final AviationAerReportingObligationService aviationAerReportingObligationService;

    @Transactional
    public void createRequestAviationAer(Long accountId) {
        Year reportingYear = Year.now().minusYears(1);

        // Validate if AER creation is allowed
        validateAccountStatus(accountId);
        validateReportingYearUniqueness(accountId, reportingYear);

        AviationAccountInfoDTO accountInfo = aviationAccountQueryService.getAviationAccountInfoDTOById(accountId);

        //create
        Request createdRequest = createRequestAviationAer(accountId, accountInfo.getEmissionTradingScheme(), reportingYear);

        //PMRV-6545 - if the account for which the aer is created is not required to report then created request will be marked as exempted
        if(AviationAccountReportingStatus.REQUIRED_TO_REPORT != accountInfo.getReportingStatus()) {
            aviationAerReportingObligationService.markAsExempt(createdRequest, null);
        }
    }

    private Request createRequestAviationAer(Long accountId, EmissionTradingScheme emissionTradingScheme, Year reportingYear) {
        RequestParams requestParams = getRequestParamsBuilderService(emissionTradingScheme)
            .map(service -> service.buildRequestParams(accountId, reportingYear))
            .orElseThrow(() -> new BusinessException(MetsErrorCode.NO_AVIATION_AER_SERVICE_FOUND));

        return startProcessRequestService.startProcess(requestParams);
    }

    private Optional<AviationAerCreationRequestParamsBuilderService> getRequestParamsBuilderService(EmissionTradingScheme scheme) {
        return aviationAerCreationRequestParamsBuilderServices.stream()
            .filter(service -> scheme.equals(service.getEmissionTradingScheme()))
            .findFirst();
    }

    private void validateAccountStatus(Long accountId) {
        RequestCreateValidationResult validationResult = aviationAerCreationValidatorService.validateAccountStatus(accountId);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.AVIATION_AER_CREATION_NOT_ALLOWED_INVALID_ACCOUNT_STATUS, validationResult);
        }
    }

    private void validateReportingYearUniqueness(Long accountId, Year reportingYear) {
        RequestCreateValidationResult validationResult =
            aviationAerCreationValidatorService.validateReportingYear(accountId, reportingYear);
        if(!validationResult.isValid()) {
            throw new BusinessException(MetsErrorCode.AVIATION_AER_ALREADY_EXISTS_FOR_REPORTING_YEAR, validationResult);
        }
    }
}

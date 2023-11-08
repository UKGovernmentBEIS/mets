package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.aviation.domain.enumeration.AviationAccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.domain.AviationAerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;

import java.time.Year;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AviationAerCreationValidatorService {

    private final RequestCreateValidatorService requestCreateValidatorService;
    private final RequestQueryService requestQueryService;
    private final AviationAerRequestIdGenerator aviationAerRequestIdGenerator;

    @Transactional
    public RequestCreateValidationResult validateAccountStatus(Long accountId) {
        Set<AccountStatus> applicableAccountStatuses = Set.of(AviationAccountStatus.NEW, AviationAccountStatus.LIVE);
        return requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of());
    }

    @Transactional
    public RequestCreateValidationResult validateReportingYear(Long accountId, Year year) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        // Validate AERs with same year
        RequestParams params = RequestParams.builder()
            .accountId(accountId)
            .requestMetadata(AviationAerRequestMetadata.builder().type(RequestMetadataType.AVIATION_AER).year(year).build())
            .build();
        String requestId = aviationAerRequestIdGenerator.generate(params);
        boolean aerExists = requestQueryService.existsRequestById(requestId);

        if (aerExists) {
            validationResult.setValid(false);
        }

        return validationResult;
    }
}

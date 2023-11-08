package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerRequestMetadata;

import java.time.Year;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AerCreationValidatorService {

    private final RequestQueryService requestQueryService;
    private final AerRequestIdGenerator aerRequestIdGenerator;
    private final RequestCreateValidatorService requestCreateValidatorService;

    @Transactional
    public RequestCreateValidationResult validateYear(Long accountId, Year year) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        // Validate AERs with same year
        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(AerRequestMetadata.builder().type(RequestMetadataType.AER).year(year).build())
                .build();
        String requestId = aerRequestIdGenerator.generate(params);
        boolean aerExists = requestQueryService.existsRequestById(requestId);

        if (aerExists) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(Set.of(RequestType.AER));
        }

        return validationResult;
    }

    @Transactional
    public RequestCreateValidationResult validateAccountStatus(Long accountId) {
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        return requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of());
    }
}

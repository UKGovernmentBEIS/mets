package uk.gov.pmrv.api.workflow.request.flow.installation.bdr.service;


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
import uk.gov.pmrv.api.workflow.request.flow.installation.bdr.domain.BDRRequestMetadata;

import java.time.Year;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BDRCreationValidationService {

    private final RequestCreateValidatorService requestCreateValidatorService;
    private final BDRRequestIdGenerator bdrRequestIdGenerator;
    private final RequestQueryService requestQueryService;

    @Transactional
    public RequestCreateValidationResult validateYear(Long accountId, Year year) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(BDRRequestMetadata.builder().type(RequestMetadataType.BDR).year(year).build())
                .build();

        String requestId = bdrRequestIdGenerator.generate(params);
        boolean bdrExists = requestQueryService.existsRequestById(requestId);

        if (bdrExists) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(Set.of(RequestType.BDR));
        }

        return validationResult;
    }

    @Transactional
    public RequestCreateValidationResult validateAccountStatus(Long accountId) {
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        return requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of());
    }
}

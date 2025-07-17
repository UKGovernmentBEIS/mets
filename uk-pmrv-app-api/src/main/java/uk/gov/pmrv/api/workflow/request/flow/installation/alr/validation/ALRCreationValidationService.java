package uk.gov.pmrv.api.workflow.request.flow.installation.alr.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.domain.ALRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.alr.service.ALRRequestIdGenerator;

import java.time.Year;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ALRCreationValidationService {

    private final RequestCreateValidatorService requestCreateValidatorService;
    private final ALRRequestIdGenerator alrRequestIdGenerator;
    private final RequestQueryService requestQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;

    @Transactional
    public RequestCreateValidationResult validateYear(Long accountId, Year year) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(ALRRequestMetaData.builder().type(RequestMetadataType.ALR).year(year).build())
                .build();

        String requestId = alrRequestIdGenerator.generate(params);
        boolean alrExists = requestQueryService.existsRequestById(requestId);

        if (alrExists) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(Set.of(RequestType.ALR));
        }

        return validationResult;
    }

    @Transactional
    public RequestCreateValidationResult validateAccountStatus(Long accountId) {
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        return requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of());
    }

    @Transactional
    public RequestCreateValidationResult validateAccountEmitterTypeAndFreeAllocations(Long accountId) {
        InstallationAccountDTO accountDTO = installationAccountQueryService.getAccountDTOById(accountId);

        return RequestCreateValidationResult.builder().valid(Objects.equals(EmitterType.GHGE,accountDTO.getEmitterType()) && accountDTO.getFaStatus()).build();
    }
}

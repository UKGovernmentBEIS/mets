package uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.validation;

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
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRQuarter;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.domain.WasteQDRRequestMetaData;
import uk.gov.pmrv.api.workflow.request.flow.installation.wasteqdr.service.WasteQDRRequestIdGenerator;

import java.time.Year;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WasteQDRCreationValidationService {

    private final RequestCreateValidatorService requestCreateValidatorService;
    private final WasteQDRRequestIdGenerator wasteQDRRequestIdGenerator;
    private final RequestQueryService requestQueryService;
    private final InstallationAccountQueryService installationAccountQueryService;

    @Transactional
    public RequestCreateValidationResult validateYearQuarter(Long accountId, Year year, WasteQDRQuarter quarter) {
        RequestCreateValidationResult validationResult = RequestCreateValidationResult.builder().valid(true).build();

        RequestParams params = RequestParams.builder()
                .accountId(accountId)
                .requestMetadata(WasteQDRRequestMetaData.builder().type(RequestMetadataType.WASTE_QDR).year(year).quarter(quarter).build())
                .build();

        String requestId = wasteQDRRequestIdGenerator.generate(params);
        boolean wasteQDRExists = requestQueryService.existsRequestById(requestId);

        if (wasteQDRExists) {
            validationResult.setValid(false);
            validationResult.setReportedRequestTypes(Set.of(RequestType.WASTE_QDR));
        }

        return validationResult;
    }

    @Transactional
    public RequestCreateValidationResult validateAccountStatus(Long accountId) {
        Set<AccountStatus> applicableAccountStatuses = Set.of(InstallationAccountStatus.LIVE);
        return requestCreateValidatorService.validate(accountId, applicableAccountStatuses, Set.of());
    }

    @Transactional
    public RequestCreateValidationResult validateAccountEmitterType(Long accountId) {
        InstallationAccountDTO accountDTO = installationAccountQueryService.getAccountDTOById(accountId);

        return RequestCreateValidationResult.builder().valid(Objects.equals(EmitterType.WASTE,accountDTO.getEmitterType())).build();
    }
}

package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.netz.api.configuration.domain.ConfigurationDTO;
import uk.gov.netz.api.configuration.service.ConfigurationService;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.installation.domain.enumeration.InstallationAccountStatus;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
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
    private final PermitQueryService permitQueryService;
    private final ConfigurationService configurationService;

    private static final String WASTE_AER_ENABLE_FLAG = "waste.aer.enable.flag";

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

    @Transactional
    public boolean validateAerCreationForWaste(Long accountId) {
        PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);

        if (!PermitType.WASTE.equals(permitContainer.getPermitType())) {
            return true;
        }

        return configurationService.getConfigurationByKey(WASTE_AER_ENABLE_FLAG)
                .map(ConfigurationDTO::getValue)
                .map(Object::toString)
                .map(Boolean::parseBoolean)
                .orElse(false);
    }
}

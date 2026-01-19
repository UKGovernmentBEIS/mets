package uk.gov.pmrv.api.web.orchestrator.account.installation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.account.installation.domain.dto.AccountUpdateRegistryReportingFirstYearDTO;
import uk.gov.pmrv.api.account.installation.domain.dto.InstallationAccountDTO;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountQueryService;
import uk.gov.pmrv.api.account.installation.service.InstallationAccountUpdateService;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.integration.registry.accountupdated.installation.request.InstallationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InstallationAccountCommandOrchestrator {

    private final PermitQueryService permitQueryService;
    private final InstallationAccountUpdateService installationAccountUpdateService;
    private final ApplicationEventPublisher publisher;
    private final InstallationAccountQueryService installationAccountQueryService;
    private final ReportableEmissionsService reportableEmissionsService;


    public void updateRegistryReportingFirstYear(Long accountId ,
                                                   AccountUpdateRegistryReportingFirstYearDTO accountUpdateRegistryReportingFirstYearDTO) {
        if(!validateRegistryReportingFirstYearValue(accountId, accountUpdateRegistryReportingFirstYearDTO)) {
            throw new BusinessException(MetsErrorCode.GHGE_REGISTRY_REPORTING_FIRST_YEAR_EMPTY_VALUE);
        }
        if(!validateAccountStatus(accountId)) {
            throw new BusinessException(MetsErrorCode.REGISTRY_REPORTING_FIRST_YEAR_INVALID_ACCOUNT_STATUS);
        }
        if(!validateAccountEmissions(accountId,accountUpdateRegistryReportingFirstYearDTO)) {
            throw new BusinessException(MetsErrorCode.REGISTRY_REPORTING_FIRST_YEAR_INVALID_EMISSIONS);
        }

        installationAccountUpdateService.updateRegistryReportingFirstYear(accountId, accountUpdateRegistryReportingFirstYearDTO);
        publisher.publishEvent(InstallationAccountUpdatedRegistryEvent.builder().accountId(accountId).build());

    }

    private boolean validateRegistryReportingFirstYearValue(Long accountId,AccountUpdateRegistryReportingFirstYearDTO accountUpdateRegistryReportingFirstYearDTO) {
        try {
            PermitContainer permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
            if (permitContainer != null && PermitType.GHGE.equals(permitContainer.getPermitType())
                    && ObjectUtils.isEmpty(accountUpdateRegistryReportingFirstYearDTO.getRegistryReportingFirstYear())) {
                return false;
            }
        } catch (BusinessException e) {
            log.info("Unable to retrieve a permit from the database for account id {}. The registry " +
                    "reporting first year value will be updated without any additional checks", accountId);
        }
        return true;
    }

    private boolean validateAccountStatus(Long accountId) {
        InstallationAccountDTO installationAccountDTO = installationAccountQueryService.getAccountDTOById(accountId);
        return switch (installationAccountDTO.getStatus()) {
            case LIVE, AWAITING_SURRENDER, AWAITING_TRANSFER -> true;
            default -> false;
        };
    }


    private boolean validateAccountEmissions(Long accountId,AccountUpdateRegistryReportingFirstYearDTO accountUpdateRegistryReportingFirstYearDTO) {
        List<ReportableEmissionsEntity> reportableEmissions =
                reportableEmissionsService.getReportableEmissionsByAccountId(accountId);

        Integer registryReportingFirstYear = accountUpdateRegistryReportingFirstYearDTO.getRegistryReportingFirstYear();

        Optional<ReportableEmissionsEntity> reportableEmissionsEntityOptional =
            reportableEmissions.stream().filter(r-> ObjectUtils.isNotEmpty(r.getYear()))
                    .min(Comparator.comparing(ReportableEmissionsEntity::getYear));

        return reportableEmissionsEntityOptional.isEmpty() || registryReportingFirstYear <= reportableEmissionsEntityOptional.get().getYear().getValue();
    }
}

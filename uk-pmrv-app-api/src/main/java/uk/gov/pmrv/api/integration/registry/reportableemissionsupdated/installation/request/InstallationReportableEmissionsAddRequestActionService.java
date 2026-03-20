package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.installation.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.web.orchestrator.account.installation.dto.InstallationAccountPermitDTO;
import uk.gov.pmrv.api.web.orchestrator.account.installation.service.InstallationAccountQueryOrchestrator;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.InstallationReportableEmissionsRegistryIntegrationRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.registryIntegration.RegistryIntegrationReportableEmissionsActivePermit;

import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class InstallationReportableEmissionsAddRequestActionService {

    private final RequestService requestService;
    private final InstallationAccountQueryOrchestrator installationAccountQueryOrchestrator;
    private final PermitQueryService permitQueryService;

    public void addRequestAction(final String requestId,
                                 InstallationReportableEmissionsRequestActionDTO installationReportableEmissionsRequestActionDTO,
                                 Long accountId) {
        Request request = requestService.findRequestById(requestId);
        if (request == null) {
            log.info("No requestId found: %s ", requestId);
            return;
        }
        InstallationAccountPermitDTO installationAccountPermitDTO = installationAccountQueryOrchestrator.getAccountWithPermit(accountId);
        PermitContainer permitContainer;
        try {
            permitContainer = permitQueryService.getPermitContainerByAccountId(accountId);
        } catch (BusinessException e) {
            log.info("Permit not found for account with id {}",accountId);
            return;
        }
        RegistryIntegrationReportableEmissionsActivePermit activePermit =
                buildRegistryIntegrationReportableEmissionsActivePermit(installationReportableEmissionsRequestActionDTO,
                        installationAccountPermitDTO, permitContainer);

        InstallationReportableEmissionsRegistryIntegrationRequestActionPayload payload =
                InstallationReportableEmissionsRegistryIntegrationRequestActionPayload.builder()
                        .activePermit(activePermit)
                        .payloadType(RequestActionPayloadType.INSTALLATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY_PAYLOAD)
                        .build();

        requestService.addSystemActionToRequest(request, payload, RequestActionType.INSTALLATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY);
    }

    private RegistryIntegrationReportableEmissionsActivePermit buildRegistryIntegrationReportableEmissionsActivePermit(InstallationReportableEmissionsRequestActionDTO installationReportableEmissionsRequestActionDTO,
                                                                                                                       InstallationAccountPermitDTO installationAccountPermitDTO,
                                                                                                                       PermitContainer permitContainer) {
        return RegistryIntegrationReportableEmissionsActivePermit.builder()
                .registryId(installationReportableEmissionsRequestActionDTO.getRegistryId())
                .permitId(installationAccountPermitDTO.getPermit().getId())
                .installationName(installationReportableEmissionsRequestActionDTO.getInstallationName())
                .operatorName(permitContainer.getInstallationOperatorDetails().getOperator())
                .firstYearOfReportingObligation(installationAccountPermitDTO.getAccount().getRegistryReportingFirstYear())
                .regulatedActivity(installationAccountPermitDTO.getPermit().getRegulatedActivities().getRegulatedActivities().stream().map(RegulatedActivity::getType).collect(Collectors.toList()))
                .reportableEmissions(installationReportableEmissionsRequestActionDTO.getReportableEmissions())
                .reportingYear(installationReportableEmissionsRequestActionDTO.getReportingYear())
                .build();
    }
}

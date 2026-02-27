package uk.gov.pmrv.api.integration.registry.reportableemissionsupdated.aviation.request.requestaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.account.aviation.domain.dto.AviationAccountDTO;
import uk.gov.pmrv.api.account.aviation.service.AviationAccountQueryService;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.emissionsmonitoringplan.common.service.EmissionsMonitoringPlanQueryService;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEtsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.emissions.updated.enabled", havingValue = "true", matchIfMissing = false)
public class AviationReportableEmissionsAddRequestActionService {

    private final RequestService requestService;
    private final EmissionsMonitoringPlanQueryService emissionsMonitoringPlanQueryService;
    private final AviationAccountQueryService aviationAccountQueryService;

    public void addRequestAction(final String requestId, Long accountId, AviationReportableEmissionsUpdatedEvent event) {
        Request request = requestService.findRequestById(requestId);
        if (request == null) {
            log.info("No requestId found: %s ", requestId);
            return;
        }
        final AviationAccountDTO accountDTO = aviationAccountQueryService.getAviationAccountDTOById(accountId);

        String empId = emissionsMonitoringPlanQueryService
                .getEmissionsMonitoringPlanUkEtsDTOByAccountId(accountId)
                .map(EmissionsMonitoringPlanUkEtsDTO::getId)
                .orElseThrow(() -> new BusinessException(MetsErrorCode.NO_EMP_SERVICE_FOUND));

        AviationReportableEmissionsOperatorDetails aviationReportableEmissionsOperatorDetails = AviationReportableEmissionsOperatorDetails.builder()
                .registryId(accountDTO.getRegistryId())
                .emissionsPlanId(empId)
                .operatorName(accountDTO.getName())
                .firstYearOfReportingObligation(accountDTO.getCommencementDate().getYear())
                .reportableEmissions(String.valueOf(event.getReportableEmissions()))
                .reportingYear(event.getYear())
                .build();

        AviationReportableEmissionsRegistryIntegrationRequestActionPayload payload =
                AviationReportableEmissionsRegistryIntegrationRequestActionPayload.builder()
                        .operatorDetails(aviationReportableEmissionsOperatorDetails)
                        .payloadType(RequestActionPayloadType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY_PAYLOAD)
                        .build();

        requestService.addSystemActionToRequest(request, payload, RequestActionType.AVIATION_REPORTABLE_EMISSIONS_SENT_TO_REGISTRY);
    }
}

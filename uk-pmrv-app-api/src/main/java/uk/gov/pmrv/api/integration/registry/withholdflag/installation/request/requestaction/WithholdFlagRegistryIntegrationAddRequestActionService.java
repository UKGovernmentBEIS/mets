package uk.gov.pmrv.api.integration.registry.withholdflag.installation.request.requestaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import uk.gov.netz.integration.model.withold.AccountWithholdUpdateEvent;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;


@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "registry.integration.withhold.flag.enabled", havingValue = "true", matchIfMissing = false)
public class WithholdFlagRegistryIntegrationAddRequestActionService {

    private final RequestService requestService;

    public void addRequestAction(String requestId, AccountWithholdUpdateEvent accountWithholdUpdateEvent) {

        Request request = requestService.findRequestById(requestId);

        WithholdingOfAllowancesRegistryIntegrationRequestActionPayload actionPayload =
                WithholdingOfAllowancesRegistryIntegrationRequestActionPayload.builder()
                        .withholdFlag(accountWithholdUpdateEvent.getWithholdFlag())
                        .registryId(Math.toIntExact(accountWithholdUpdateEvent.getRegistryId()))
                        .withholdYear(accountWithholdUpdateEvent.getReportingYear())
                        .payloadType(RequestActionPayloadType.WITHHOLDING_OF_ALLOWANCES_REGISTRY_INTEGRATION_PAYLOAD)
                        .build();

        requestService.addSystemActionToRequest(request,
                actionPayload,
                RequestActionType.WITHHOLDING_OF_ALLOWANCES_SENT_TO_REGISTRY);

    }


}

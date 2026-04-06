package uk.gov.pmrv.api.integration.registry.setoperator.aviation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.EmissionsMonitoringPlanUkEts;
import uk.gov.pmrv.api.integration.registry.accountupdated.aviation.request.AviationAccountUpdatedRegistryEvent;
import uk.gov.pmrv.api.web.orchestrator.account.aviation.service.AviationAccountEmpCommandOrchestrator;


@Service
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "registry.integration.set.operator.id.enabled", havingValue = "true", matchIfMissing = false)
public class AviationSetOperatorIdAccountUpdateService {

    private final AviationAccountEmpCommandOrchestrator aviationAccountEmpCommandOrchestrator;
    private final ApplicationEventPublisher publisher;

    public void notifyRegistryWithAccountUpdate(Long accountId) {
        EmissionsMonitoringPlanUkEts emissionsMonitoringPlanUkEts =
                aviationAccountEmpCommandOrchestrator.getEmpFromPendingApprovalRequest(accountId);

        publisher.publishEvent(AviationAccountUpdatedRegistryEvent.builder()
                .accountId(accountId)
                .emissionsMonitoringPlan(emissionsMonitoringPlanUkEts)
                .isFromSetOperatorId(true)
                .build());

    }

}

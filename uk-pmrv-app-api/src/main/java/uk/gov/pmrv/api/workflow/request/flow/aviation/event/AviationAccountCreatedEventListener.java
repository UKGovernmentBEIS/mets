package uk.gov.pmrv.api.workflow.request.flow.aviation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountCreatedEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service.EmissionMonitoringPlanCreationService;

@Component
@RequiredArgsConstructor
public class AviationAccountCreatedEventListener {

    private final EmissionMonitoringPlanCreationService emissionMonitoringPlanCreationService;

    @EventListener(AviationAccountCreatedEvent.class)
    public void onAviationAccountCreatedEvent(AviationAccountCreatedEvent event) {
        emissionMonitoringPlanCreationService
            .createRequestEmissionMonitoringPlan(event.getAccountId(), event.getEmissionTradingScheme());
    }
}

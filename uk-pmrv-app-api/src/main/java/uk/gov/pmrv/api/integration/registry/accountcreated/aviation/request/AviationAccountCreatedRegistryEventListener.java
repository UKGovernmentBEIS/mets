package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.AviationAccountCreatedRegistryEvent;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class AviationAccountCreatedRegistryEventListener {

    private final AviationAccountCreatedNotifyRegistryService aviationAccountCreatedNotifyRegistryService;

    @EventListener(AviationAccountCreatedRegistryEvent.class)
    public void handle(AviationAccountCreatedRegistryEvent event) {
        aviationAccountCreatedNotifyRegistryService.notifyRegistry(event);}

}

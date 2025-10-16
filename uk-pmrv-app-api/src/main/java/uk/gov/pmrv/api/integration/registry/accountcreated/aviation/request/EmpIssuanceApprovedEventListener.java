package uk.gov.pmrv.api.integration.registry.accountcreated.aviation.request;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceApprovedEvent;

@RequiredArgsConstructor
@Component
@ConditionalOnProperty(name = "registry.integration.account.creation.enabled", havingValue = "true", matchIfMissing = false)
public class EmpIssuanceApprovedEventListener {

    private final EmpIssuanceApprovedNotifyRegistryService empIssuanceApprovedNotifyRegistryService;

    @EventListener(EmpIssuanceApprovedEvent.class)
    public void handle(EmpIssuanceApprovedEvent event) {empIssuanceApprovedNotifyRegistryService.notifyRegistry(event);}

}

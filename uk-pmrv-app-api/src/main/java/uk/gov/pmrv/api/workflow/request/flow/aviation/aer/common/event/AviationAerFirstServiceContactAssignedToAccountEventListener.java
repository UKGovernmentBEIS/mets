package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerContactUpdateService;

@Component
@RequiredArgsConstructor
public class AviationAerFirstServiceContactAssignedToAccountEventListener {

    private final AviationAerContactUpdateService serviceContactUpdateService;

    @EventListener(FirstServiceContactAssignedToAccountEvent.class)
    public void onFirstServiceContactAssignedToAccountEvent(FirstServiceContactAssignedToAccountEvent event) {
        serviceContactUpdateService.updateAerAviationApplicationSubmitTaskPayloadWithServiceContactDetails(event.getAccountId(),
                event.getServiceContactDetails());
    }
}

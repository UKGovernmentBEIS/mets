package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.event.FirstServiceContactAssignedToAccountEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.service.EmpContactUpdateService;

@Component
@RequiredArgsConstructor
public class EmpFirstServiceContactAssignedToAccountEventListener {

    private final EmpContactUpdateService serviceContactUpdateService;

    @EventListener(FirstServiceContactAssignedToAccountEvent.class)
    public void onFirstServiceContactAssignedToAccountEvent(FirstServiceContactAssignedToAccountEvent event) {
        serviceContactUpdateService.updateEmpApplicationSubmitTaskPayloadWithServiceContactDetails(event.getAccountId(),
                event.getServiceContactDetails());
    }
}

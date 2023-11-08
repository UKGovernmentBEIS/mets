package uk.gov.pmrv.api.workflow.request.application.accountcontactassigned;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.domain.event.FirstPrimaryContactAssignedToAccountEvent;

@Component
@RequiredArgsConstructor
public class FirstPrimaryContactAssignedToAccountEventListener {

    private final FirstPrimaryContactAssignedToAccountEventService firstPrimaryContactAssignedToAccountEventService;

    @EventListener(FirstPrimaryContactAssignedToAccountEvent.class)
    public void onFirstPrimaryContactAssignedToAccountEvent(FirstPrimaryContactAssignedToAccountEvent event) {
        firstPrimaryContactAssignedToAccountEventService
            .assignUnassignedTasksToAccountPrimaryContact(event.getAccountId(), event.getUserId());
    }
}

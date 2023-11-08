package uk.gov.pmrv.api.workflow.request.flow.installation.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.installation.domain.InstallationAccountStatusChangedEvent;
import uk.gov.pmrv.api.workflow.request.flow.installation.ParallelRequestHandler;

@RequiredArgsConstructor
@Component
public class InstallationAccountStatusChangedEventListener {

    private final ParallelRequestHandler handler;

    @EventListener(InstallationAccountStatusChangedEvent.class)
    public void onAccountStatusChangedEvent(InstallationAccountStatusChangedEvent event) {
        handler.handleParallelRequests(event.getAccountId(), event.getStatus());
    }
}

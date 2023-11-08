package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingRequiredEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerReportingObligationService;

@Component
@RequiredArgsConstructor
public class AviationAccountReportingRequiredEventListener {

    private final AviationAerReportingObligationService aviationAerReportingObligationService;

    @EventListener(AviationAccountReportingRequiredEvent.class)
    public void onAviationAccountReportingRequiredEvent(AviationAccountReportingRequiredEvent event) {
        aviationAerReportingObligationService.revertExemption(event.getAccountId(), event.getSubmitterId());
    }
}

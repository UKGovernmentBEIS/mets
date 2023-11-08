package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingExemptEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerReportingObligationService;

@Component
@RequiredArgsConstructor
public class AviationAccountReportingExemptEventListener {

    private final AviationAerReportingObligationService aviationAerReportingObligationService;

    @EventListener(AviationAccountReportingExemptEvent.class)
    public void onAviationAccountReportingExemptEvent(AviationAccountReportingExemptEvent event) {
        aviationAerReportingObligationService.markAsExempt(event.getAccountId(), event.getSubmitterId());
    }
}

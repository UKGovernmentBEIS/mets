package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusCreatedEvent;

@Service
@RequiredArgsConstructor
public class AviationAerCreationEventListener {

    private final AviationAerCreationService aviationAerCreationService;

    @EventListener
    public void onReportingStatusCreated(AviationAccountReportingStatusCreatedEvent event) {
        aviationAerCreationService.createAerFromFirstYearOfReportingObligation(
                event.getAccountId(), event.getYear(), event.getEmissionTradingScheme());
    }
}
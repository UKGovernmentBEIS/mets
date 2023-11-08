package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingExemptEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerReportingObligationService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingExemptEventListenerTest {

    @InjectMocks
    private AviationAccountReportingExemptEventListener eventListener;

    @Mock
    private AviationAerReportingObligationService aviationAerReportingObligationService;

    @Test
    void onAviationAccountReportingExemptedEvent() {
        Long accountId = 1L;
        String userId = "userId";

        AviationAccountReportingExemptEvent event = AviationAccountReportingExemptEvent.builder()
            .accountId(accountId)
            .submitterId(userId)
            .build();

        eventListener.onAviationAccountReportingExemptEvent(event);

        verify(aviationAerReportingObligationService, times(1)).markAsExempt(accountId, userId);
    }
}
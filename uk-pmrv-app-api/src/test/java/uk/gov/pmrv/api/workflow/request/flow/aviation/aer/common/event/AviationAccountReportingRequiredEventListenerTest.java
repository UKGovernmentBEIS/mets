package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.event;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingRequiredEvent;
import uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service.AviationAerReportingObligationService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAccountReportingRequiredEventListenerTest {

    @InjectMocks
    private AviationAccountReportingRequiredEventListener eventListener;

    @Mock
    private AviationAerReportingObligationService aviationAerReportingObligationService;


    @Test
    void onAviationAccountReportingRequiredEvent() {
        Long accountId = 1L;
        String userId = "userId";

        AviationAccountReportingRequiredEvent event = AviationAccountReportingRequiredEvent.builder()
            .accountId(accountId)
            .submitterId(userId)
            .build();

        eventListener.onAviationAccountReportingRequiredEvent(event);

        verify(aviationAerReportingObligationService, times(1)).revertExemption(accountId, userId);
    }
}
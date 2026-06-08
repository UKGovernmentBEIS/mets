package uk.gov.pmrv.api.workflow.request.flow.aviation.aer.common.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.aviation.domain.AviationAccountReportingStatusCreatedEvent;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.time.Year;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AviationAerCreationEventListenerTest {

    @InjectMocks
    private AviationAerCreationEventListener listener;

    @Mock
    private AviationAerCreationService aviationAerCreationService;

    @Test
    void onReportingStatusCreated() {
        Long accountId = 1L;
        Year year = Year.of(2024);
        EmissionTradingScheme scheme = EmissionTradingScheme.UK_ETS_AVIATION;

        AviationAccountReportingStatusCreatedEvent event = AviationAccountReportingStatusCreatedEvent.builder()
                .accountId(accountId)
                .year(year)
                .emissionTradingScheme(scheme)
                .build();

        listener.onReportingStatusCreated(event);

        verify(aviationAerCreationService).createAerFromFirstYearOfReportingObligation(accountId, year, scheme);
    }
}
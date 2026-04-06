package uk.gov.pmrv.api.integration.registry.exemptstatus.aviation.response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.aviationreporting.common.service.AviationReportableEmissionsService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAccountExemptFlagEmissionsPublishServiceTest {

    @InjectMocks
    private AviationAccountExemptFlagEmissionsPublishService cut;

    @Mock
    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void publishEmissions_whenEmissionsExist_shouldPublishEvent() {
        Long accountId = 1L;
        Year year = Year.of(2023);
        BigDecimal emissions = new BigDecimal("500.50");

        AviationReportableEmissionsEntity entity = AviationReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(emissions)
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsService.getReportableEmissionsForYear(accountId, year))
                .thenReturn(Optional.of(entity));

        cut.publishEmissions(accountId, year);

        ArgumentCaptor<AviationReportableEmissionsUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(AviationReportableEmissionsUpdatedEvent.class);

        verify(aviationReportableEmissionsService).getReportableEmissionsForYear(accountId, year);
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        AviationReportableEmissionsUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(year, publishedEvent.getYear());
        assertEquals(emissions, publishedEvent.getReportableEmissions());
        assertEquals(false, publishedEvent.isFromDre());
        assertTrue(publishedEvent.isHistorical()); // Verify isHistorical is set to true as per code
    }

    @Test
    void publishEmissions_whenEmissionsDoNotExist_shouldNotPublishEvent() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        when(aviationReportableEmissionsService.getReportableEmissionsForYear(accountId, year))
                .thenReturn(Optional.empty());

        cut.publishEmissions(accountId, year);

        verify(aviationReportableEmissionsService).getReportableEmissionsForYear(accountId, year);
        verify(applicationEventPublisher, never()).publishEvent(any());
    }
}
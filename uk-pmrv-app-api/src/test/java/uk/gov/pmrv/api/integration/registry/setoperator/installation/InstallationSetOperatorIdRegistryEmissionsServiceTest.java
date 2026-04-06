package uk.gov.pmrv.api.integration.registry.setoperator.installation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.reporting.domain.InstallationReportableEmissionsUpdatedEvent;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.service.ReportableEmissionsService;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstallationSetOperatorIdRegistryEmissionsServiceTest {

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private ReportableEmissionsService reportableEmissionsService;

    @InjectMocks
    private InstallationSetOperatorIdRegistryEmissionsService service;

    @Test
    void notifyRegistryWithEmissions_publishesEventForSingleEntity() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        ReportableEmissionsEntity entity = ReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(false)
                .build();

        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(entity));

        service.notifyRegistryWithEmissions(accountId);

        ArgumentCaptor<InstallationReportableEmissionsUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(InstallationReportableEmissionsUpdatedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InstallationReportableEmissionsUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(year, publishedEvent.getYear());
        assertEquals(BigDecimal.valueOf(1000), publishedEvent.getReportableEmissions());
        assertFalse(publishedEvent.isFromDre());
        assertTrue(publishedEvent.isSetOperatorId());
    }

    @Test
    void notifyRegistryWithEmissions_publishesDreEvent() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        ReportableEmissionsEntity dreEntity = ReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(true)
                .build();

        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(dreEntity));

        service.notifyRegistryWithEmissions(accountId);

        ArgumentCaptor<InstallationReportableEmissionsUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(InstallationReportableEmissionsUpdatedEvent.class);
        verify(applicationEventPublisher, times(1)).publishEvent(eventCaptor.capture());

        InstallationReportableEmissionsUpdatedEvent publishedEvent = eventCaptor.getValue();
        assertEquals(accountId, publishedEvent.getAccountId());
        assertEquals(year, publishedEvent.getYear());
        assertEquals(BigDecimal.valueOf(2000), publishedEvent.getReportableEmissions());
        assertTrue(publishedEvent.isFromDre());
        assertTrue(publishedEvent.isSetOperatorId());
    }

    @Test
    void notifyRegistryWithEmissions_publishesEventsForMultipleEntities() {
        Long accountId = 1L;
        Year year2023 = Year.of(2023);
        Year year2022 = Year.of(2022);

        ReportableEmissionsEntity entity2023 = ReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year2023)
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(false)
                .build();

        ReportableEmissionsEntity entity2022 = ReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year2022)
                .reportableEmissions(BigDecimal.valueOf(800))
                .isFromDre(true)
                .build();

        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of(entity2023, entity2022));

        service.notifyRegistryWithEmissions(accountId);

        ArgumentCaptor<InstallationReportableEmissionsUpdatedEvent> eventCaptor =
                ArgumentCaptor.forClass(InstallationReportableEmissionsUpdatedEvent.class);
        verify(applicationEventPublisher, times(2)).publishEvent(eventCaptor.capture());

        List<InstallationReportableEmissionsUpdatedEvent> events = eventCaptor.getAllValues();

        InstallationReportableEmissionsUpdatedEvent event2023 = events.get(0);
        assertEquals(year2023, event2023.getYear());
        assertEquals(BigDecimal.valueOf(1000), event2023.getReportableEmissions());
        assertFalse(event2023.isFromDre());
        assertTrue(event2023.isSetOperatorId());

        InstallationReportableEmissionsUpdatedEvent event2022 = events.get(1);
        assertEquals(year2022, event2022.getYear());
        assertEquals(BigDecimal.valueOf(800), event2022.getReportableEmissions());
        assertTrue(event2022.isFromDre());
        assertTrue(event2022.isSetOperatorId());
    }

    @Test
    void notifyRegistryWithEmissions_noEmissionsEntities_nothingPublished() {
        Long accountId = 1L;

        when(reportableEmissionsService.getReportableEmissionsByAccountId(accountId))
                .thenReturn(List.of());

        service.notifyRegistryWithEmissions(accountId);

        verify(applicationEventPublisher, never()).publishEvent(
                org.mockito.ArgumentMatchers.any(InstallationReportableEmissionsUpdatedEvent.class));
    }
}


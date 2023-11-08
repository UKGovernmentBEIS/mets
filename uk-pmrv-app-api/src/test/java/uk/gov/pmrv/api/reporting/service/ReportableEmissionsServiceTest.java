package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsEntity;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.repository.ReportableEmissionsRepository;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportableEmissionsServiceTest {

    @InjectMocks
    private ReportableEmissionsService reportableEmissionsService;

    @Mock
    private ReportableEmissionsRepository reportableEmissionsRepository;

    @Test
    void getReportableEmissions() {
        final long accountId = 1L;
        final Set<Year> years = Set.of(Year.of(2021), Year.of(2022), Year.of(2023));
        final List<ReportableEmissionsEntity> entities = List.of(
                ReportableEmissionsEntity.builder().id(1L).accountId(accountId).year(Year.of(2021))
                        .reportableEmissions(BigDecimal.valueOf(2000)).isFromDre(false).build(),
                ReportableEmissionsEntity.builder().id(2L).accountId(accountId).year(Year.of(2022))
                        .reportableEmissions(BigDecimal.valueOf(4000)).isFromDre(false).build()
        );
        final Map<Year, BigDecimal> expected = Map.of(
                Year.of(2021), BigDecimal.valueOf(2000),
                Year.of(2022), BigDecimal.valueOf(4000)
        );

        when(reportableEmissionsRepository.findAllByAccountIdAndYearIn(accountId, years))
                .thenReturn(entities);

        // Invoke
        Map<Year, BigDecimal> actual = reportableEmissionsService.getReportableEmissions(accountId, years);

        // Verify
        assertEquals(expected, actual);
        verify(reportableEmissionsRepository, times(1))
                .findAllByAccountIdAndYearIn(accountId, years);
    }

    @Test
    void saveReportableEmissions() {
        final ReportableEmissionsSaveParams params = ReportableEmissionsSaveParams.builder()
                .accountId(1L)
                .year(Year.now())
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(false)
                .build();
        final ReportableEmissionsEntity entity = ReportableEmissionsEntity.builder()
                .id(100L)
                .accountId(params.getAccountId())
                .year(params.getYear())
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(false)
                .build();

        when(reportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(entity));

        // Invoke
        reportableEmissionsService.saveReportableEmissions(params);

        // Verify
        assertNotNull(entity);
        assertEquals(100L, entity.getId());
        assertEquals(BigDecimal.valueOf(1000), entity.getReportableEmissions());
    }

    @Test
    void saveReportableEmissions_new_emissions() {
        final ReportableEmissionsSaveParams params = ReportableEmissionsSaveParams.builder()
                .accountId(1L)
                .year(Year.now())
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(false)
                .build();

        when(reportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.empty());

        // Invoke
        reportableEmissionsService.saveReportableEmissions(params);

        // Verify
        ArgumentCaptor<ReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(ReportableEmissionsEntity.class);
        verify(reportableEmissionsRepository, times(1)).save(emissionsArgumentCaptor.capture());
        ReportableEmissionsEntity savedEntity = emissionsArgumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(params.getAccountId(), savedEntity.getAccountId());
        assertEquals(params.getYear(), savedEntity.getYear());
        assertEquals(BigDecimal.valueOf(1000), savedEntity.getReportableEmissions());
        assertEquals(params.isFromDre(), savedEntity.isFromDre());
    }

    @Test
    void saveReportableEmissions_aer_not_from_dre() {
        final ReportableEmissionsSaveParams params = ReportableEmissionsSaveParams.builder()
                .accountId(1L)
                .year(Year.now())
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(false)
                .build();
        final ReportableEmissionsEntity entity = ReportableEmissionsEntity.builder()
                .id(100L)
                .accountId(params.getAccountId())
                .year(params.getYear())
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(true)
                .build();

        when(reportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(entity));

        // Invoke
        reportableEmissionsService.saveReportableEmissions(params);

        // Verify
        verify(reportableEmissionsRepository, never()).save(any());
    }

    @Test
    void saveReportableEmissions_from_dre() {
        final ReportableEmissionsSaveParams params = ReportableEmissionsSaveParams.builder()
                .accountId(1L)
                .year(Year.now())
                .reportableEmissions(BigDecimal.valueOf(1000))
                .isFromDre(true)
                .build();

        final ReportableEmissionsEntity entity = ReportableEmissionsEntity.builder()
                .id(100L)
                .accountId(params.getAccountId())
                .year(params.getYear())
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(false)
                .build();

        when(reportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(entity));

        // Invoke
        reportableEmissionsService.saveReportableEmissions(params);

        // Verify
        ArgumentCaptor<ReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(ReportableEmissionsEntity.class);

        assertNotNull(entity);
        assertEquals(params.getAccountId(), entity.getAccountId());
        assertEquals(params.getYear(), entity.getYear());
        assertEquals(BigDecimal.valueOf(1000), entity.getReportableEmissions());
        assertEquals(params.isFromDre(), entity.isFromDre());
    }
}

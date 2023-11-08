package uk.gov.pmrv.api.aviationreporting.corsia.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.math.BigDecimal;
import java.time.Year;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationCorsiaReportableEmissionsUpdateServiceTest {

    @InjectMocks
    private AviationCorsiaReportableEmissionsUpdateService aviationCorsiaReportableEmissionsUpdateService;

    @Mock
    private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Test
    void saveReportableEmissions_new_entry() {
        Long accountId = 1L;
        Year year = Year.now();
        BigDecimal reportableEmissions = BigDecimal.valueOf(500);
        BigDecimal reportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal reportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions)
                        .reportableOffsetEmissions(reportableOffsetEmissions)
                        .reportableReductionClaimEmissions(reportableReductionClaimEmissions)
                        .build())
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)).thenReturn(Optional.empty());

        // Invoke
        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        // Verify
        ArgumentCaptor<AviationReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(AviationReportableEmissionsEntity.class);
        verify(aviationReportableEmissionsRepository, times(1)).save(emissionsArgumentCaptor.capture());
        AviationReportableEmissionsEntity savedEntity = emissionsArgumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(accountId, savedEntity.getAccountId());
        assertEquals(year, savedEntity.getYear());
        assertEquals(reportableEmissions, savedEntity.getReportableEmissions());
        assertEquals(reportableOffsetEmissions, savedEntity.getReportableOffsetEmissions());
        assertEquals(reportableReductionClaimEmissions, savedEntity.getReportableReductionClaimEmissions());
        assertEquals(params.isFromDre(), savedEntity.isFromDre());
    }

    @Test
    void saveReportableEmissions_when_aer_and_entry_from_aer_exists() {
        Long accountId = 1L;
        Year year = Year.now();
        Long reportableEmissionsEntityId = 10L;
        BigDecimal newReportableEmissions = BigDecimal.valueOf(500);
        BigDecimal newReportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal newReportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(newReportableEmissions)
                        .reportableOffsetEmissions(newReportableOffsetEmissions)
                        .reportableReductionClaimEmissions(newReportableReductionClaimEmissions)
                        .build())
                .isFromDre(false)
                .build();
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(reportableEmissionsEntityId)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(200))
                .reportableOffsetEmissions(BigDecimal.valueOf(100))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(100))
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)).thenReturn(Optional.of(reportableEmissionsEntity));

        // Invoke
        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        // Verify
        assertEquals(reportableEmissionsEntityId, reportableEmissionsEntity.getId());
        assertEquals(newReportableEmissions, reportableEmissionsEntity.getReportableEmissions());
        assertEquals(newReportableOffsetEmissions, reportableEmissionsEntity.getReportableOffsetEmissions());
        assertEquals(newReportableReductionClaimEmissions, reportableEmissionsEntity.getReportableReductionClaimEmissions());

        verify(aviationReportableEmissionsRepository, never()).save(any());
    }

    @Test
    void saveReportableEmissions_when_aer_and_entry_from_dre_exist() {
        Long accountId = 1L;
        Year year = Year.now();
        Long reportableEmissionsEntityId = 10L;
        BigDecimal newReportableEmissions = BigDecimal.valueOf(500);
        BigDecimal newReportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal newReportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(newReportableEmissions)
                        .reportableOffsetEmissions(newReportableOffsetEmissions)
                        .reportableReductionClaimEmissions(newReportableReductionClaimEmissions)
                        .build())
                .isFromDre(false)
                .build();
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(reportableEmissionsEntityId)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(200))
                .reportableOffsetEmissions(BigDecimal.valueOf(100))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(100))
                .isFromDre(true)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(reportableEmissionsEntity));

        // Invoke
        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        // Verify
        assertEquals(reportableEmissionsEntityId, reportableEmissionsEntity.getId());
        assertEquals(BigDecimal.valueOf(200), reportableEmissionsEntity.getReportableEmissions());
        assertEquals(BigDecimal.valueOf(100), reportableEmissionsEntity.getReportableOffsetEmissions());
        assertEquals(BigDecimal.valueOf(100), reportableEmissionsEntity.getReportableReductionClaimEmissions());

        verify(aviationReportableEmissionsRepository, never()).save(any());
    }

    @Test
    void saveReportableEmissions_when_dre_and_entry_from_dre_exist() {
        Long accountId = 1L;
        Year year = Year.now();
        Long reportableEmissionsEntityId = 10L;
        BigDecimal newReportableEmissions = BigDecimal.valueOf(500);
        BigDecimal newReportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal newReportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(newReportableEmissions)
                        .reportableOffsetEmissions(newReportableOffsetEmissions)
                        .reportableReductionClaimEmissions(newReportableReductionClaimEmissions)
                        .build())
                .isFromDre(true)
                .build();
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(reportableEmissionsEntityId)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(200))
                .reportableOffsetEmissions(BigDecimal.valueOf(100))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(100))
                .isFromDre(true)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(reportableEmissionsEntity));

        // Invoke
        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        // Verify
        assertEquals(reportableEmissionsEntityId, reportableEmissionsEntity.getId());
        assertEquals(newReportableEmissions, reportableEmissionsEntity.getReportableEmissions());
        assertEquals(newReportableOffsetEmissions, reportableEmissionsEntity.getReportableOffsetEmissions());
        assertEquals(newReportableReductionClaimEmissions, reportableEmissionsEntity.getReportableReductionClaimEmissions());

        verify(aviationReportableEmissionsRepository, never()).save(any());
    }

    @Test
    void saveReportableEmissions_when_dre_and_entry_from_aer_exist() {
        Long accountId = 1L;
        Year year = Year.now();
        Long reportableEmissionsEntityId = 10L;
        BigDecimal newReportableEmissions = BigDecimal.valueOf(500);
        BigDecimal newReportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal newReportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(newReportableEmissions)
                        .reportableOffsetEmissions(newReportableOffsetEmissions)
                        .reportableReductionClaimEmissions(newReportableReductionClaimEmissions)
                        .build())
                .isFromDre(true)
                .build();
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(reportableEmissionsEntityId)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(200))
                .reportableOffsetEmissions(BigDecimal.valueOf(100))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(100))
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(params.getAccountId(), params.getYear()))
                .thenReturn(Optional.of(reportableEmissionsEntity));

        // Invoke
        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        // Verify
        assertEquals(reportableEmissionsEntityId, reportableEmissionsEntity.getId());
        assertEquals(newReportableEmissions, reportableEmissionsEntity.getReportableEmissions());
        assertEquals(newReportableOffsetEmissions, reportableEmissionsEntity.getReportableOffsetEmissions());
        assertEquals(newReportableReductionClaimEmissions, reportableEmissionsEntity.getReportableReductionClaimEmissions());

        verify(aviationReportableEmissionsRepository, never()).save(any());
    }

    @Test
    void getEmissionTradingScheme() {
        assertThat(aviationCorsiaReportableEmissionsUpdateService.getEmissionTradingScheme())
                .isEqualTo(EmissionTradingScheme.CORSIA);
    }
}

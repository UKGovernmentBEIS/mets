package uk.gov.pmrv.api.aviationreporting.corsia.service;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsUpdatedEvent;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationCorsiaReportableEmissionsUpdateServiceTest {

    @InjectMocks
    private AviationCorsiaReportableEmissionsUpdateService aviationCorsiaReportableEmissionsUpdateService;

    @Mock
    private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;
    
    @Mock
    private ApplicationEventPublisher publisher;

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
        
        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(reportableEmissions)
                .isFromDre(false)
        		.build());
    }

    @Test
    void saveReportableEmissions_newEntry_reportableEmissionsNull_doNotUpdateReportableEmissions() {
        Long accountId = 1L;
        Year year = Year.now();

        BigDecimal reportableOffsetEmissions = BigDecimal.valueOf(400);
        BigDecimal reportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(null)
                        .reportableOffsetEmissions(reportableOffsetEmissions)
                        .reportableReductionClaimEmissions(reportableReductionClaimEmissions)
                        .build())
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)).thenReturn(Optional.empty());

        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        ArgumentCaptor<AviationReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(AviationReportableEmissionsEntity.class);
        verify(aviationReportableEmissionsRepository, times(1)).save(emissionsArgumentCaptor.capture());
        AviationReportableEmissionsEntity savedEntity = emissionsArgumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(accountId, savedEntity.getAccountId());
        assertEquals(year, savedEntity.getYear());
        assertThat(savedEntity.getReportableEmissions()).isNull();
        assertEquals(reportableOffsetEmissions, savedEntity.getReportableOffsetEmissions());
        assertEquals(reportableReductionClaimEmissions, savedEntity.getReportableReductionClaimEmissions());
        assertEquals(params.isFromDre(), savedEntity.isFromDre());

        verifyNoInteractions(publisher);
    }

    @Test
    void saveReportableEmissions_newEntry_reportableOffsetEmissionsNull_doNotUpdateReportableOffsetEmissions() {
        Long accountId = 1L;
        Year year = Year.now();

        BigDecimal reportableEmissions = BigDecimal.valueOf(500);
        BigDecimal reportableReductionClaimEmissions = BigDecimal.valueOf(100);
        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions)
                        .reportableOffsetEmissions(null)
                        .reportableReductionClaimEmissions(reportableReductionClaimEmissions)
                        .build())
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)).thenReturn(Optional.empty());

        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        ArgumentCaptor<AviationReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(AviationReportableEmissionsEntity.class);
        verify(aviationReportableEmissionsRepository, times(1)).save(emissionsArgumentCaptor.capture());
        AviationReportableEmissionsEntity savedEntity = emissionsArgumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(accountId, savedEntity.getAccountId());
        assertEquals(year, savedEntity.getYear());
        assertEquals(reportableEmissions, savedEntity.getReportableEmissions());
        assertThat(savedEntity.getReportableOffsetEmissions()).isNull();
        assertEquals(reportableReductionClaimEmissions, savedEntity.getReportableReductionClaimEmissions());
        assertEquals(params.isFromDre(), savedEntity.isFromDre());

        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(reportableEmissions)
                .isFromDre(false)
        		.build());
    }

    @Test
    void saveReportableEmissions_newEntry_reportableReductionClaimEmissionsNull_doNotUpdateReportableReductionClaimEmissions() {
        Long accountId = 1L;
        Year year = Year.now();

        BigDecimal reportableEmissions = BigDecimal.valueOf(500);
        BigDecimal reportableOffsetEmissions = BigDecimal.valueOf(400);

        AviationReportableEmissionsSaveParams params = AviationReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(year)
                .reportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions)
                        .reportableOffsetEmissions(reportableOffsetEmissions)
                        .reportableReductionClaimEmissions(null)
                        .build())
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year)).thenReturn(Optional.empty());

        aviationCorsiaReportableEmissionsUpdateService.saveReportableEmissions(params);

        ArgumentCaptor<AviationReportableEmissionsEntity> emissionsArgumentCaptor = ArgumentCaptor.forClass(AviationReportableEmissionsEntity.class);
        verify(aviationReportableEmissionsRepository, times(1)).save(emissionsArgumentCaptor.capture());
        AviationReportableEmissionsEntity savedEntity = emissionsArgumentCaptor.getValue();

        assertNotNull(savedEntity);
        assertEquals(accountId, savedEntity.getAccountId());
        assertEquals(year, savedEntity.getYear());
        assertEquals(reportableEmissions, savedEntity.getReportableEmissions());
        assertEquals(reportableOffsetEmissions, savedEntity.getReportableOffsetEmissions());
        assertThat(savedEntity.getReportableReductionClaimEmissions()).isNull();
        assertEquals(params.isFromDre(), savedEntity.isFromDre());

        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(reportableEmissions)
                .isFromDre(false)
        		.build());
    }

    @Test
    void saveReportableEmissions_params_not_from_dre_and_entity_not_from_dre() {
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
        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(newReportableEmissions)
                .isFromDre(false)
        		.build());
        
    }

    @Test
    void saveReportableEmissions_params_not_from_dre_and_entity_from_dre() {
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
        verifyNoInteractions(publisher);
    }

    @Test
    void saveReportableEmissions_params_from_dre_and_entity_from_dre() {
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
        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(newReportableEmissions)
                .isFromDre(true)
        		.build());
    }

    @Test
    void saveReportableEmissions_params_from_dre_and_entity_not_from_dre() {
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
        verify(publisher, times(1)).publishEvent(AviationReportableEmissionsUpdatedEvent.builder()
        		.accountId(accountId)
                .year(year)
                .reportableEmissions(newReportableEmissions)
                .isFromDre(true)
        		.build());
    }

    @Test
    void getEmissionTradingScheme() {
        assertThat(aviationCorsiaReportableEmissionsUpdateService.getEmissionTradingScheme())
                .isEqualTo(EmissionTradingScheme.CORSIA);
    }
}

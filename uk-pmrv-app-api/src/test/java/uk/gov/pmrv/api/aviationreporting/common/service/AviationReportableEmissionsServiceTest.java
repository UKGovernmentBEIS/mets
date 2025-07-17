package uk.gov.pmrv.api.aviationreporting.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationAerTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsEntity;
import uk.gov.pmrv.api.aviationreporting.common.domain.AviationReportableEmissionsSaveParams;
import uk.gov.pmrv.api.aviationreporting.common.domain.dto.AviationReportableEmissionsDTO;
import uk.gov.pmrv.api.aviationreporting.common.repository.AviationReportableEmissionsRepository;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationAerCorsiaReportableEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.corsia.service.AviationCorsiaReportableEmissionsUpdateService;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationAerUkEtsReportableEmissionsCalculationService;
import uk.gov.pmrv.api.aviationreporting.ukets.service.AviationUkEtsReportableEmissionsUpdateService;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;

import java.math.BigDecimal;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationReportableEmissionsServiceTest {

    private AviationReportableEmissionsService aviationReportableEmissionsService;

    @Mock
    private AviationReportableEmissionsRepository aviationReportableEmissionsRepository;

    @Mock
    private AviationAerCorsiaReportableEmissionsCalculationService aviationAerCorsiaReportableEmissionsCalculationService;

    @Mock
    private AviationAerUkEtsReportableEmissionsCalculationService aviationAerUkEtsReportableEmissionsCalculationService;

    @Mock
    private AviationCorsiaReportableEmissionsUpdateService aviationCorsiaReportableEmissionsUpdateService;

    @Mock
    private AviationUkEtsReportableEmissionsUpdateService aviationUkEtsReportableEmissionsUpdateService;

    @BeforeEach
    void setUp() {
        aviationReportableEmissionsService = new AviationReportableEmissionsService(
                aviationReportableEmissionsRepository,
                List.of(aviationAerCorsiaReportableEmissionsCalculationService, aviationAerUkEtsReportableEmissionsCalculationService),
                List.of(aviationCorsiaReportableEmissionsUpdateService, aviationUkEtsReportableEmissionsUpdateService)
        );
    }

    @Test
    void getReportableEmissions_ukets() {
        Long accountId = 1L;
        Year year2021  = Year.of(2021);
        BigDecimal reportableEmissions2021 = BigDecimal.valueOf(305.90);
        Year year2022  = Year.of(2022);
        BigDecimal reportableEmissions2022 = BigDecimal.valueOf(335.90);

        AviationReportableEmissionsEntity reportableEmissionsEntity2021 = AviationReportableEmissionsEntity.builder()
            .accountId(accountId)
            .year(year2021)
            .reportableEmissions(reportableEmissions2021)
            .build();

        AviationReportableEmissionsEntity reportableEmissionsEntity2022 = AviationReportableEmissionsEntity.builder()
            .accountId(accountId)
            .year(year2022)
            .reportableEmissions(reportableEmissions2022)
            .isExempted(true)
            .build();

        AviationReportableEmissionsDTO reportableEmissionsDTO2021 = AviationReportableEmissionsDTO.builder()
                .totalReportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions2021)
                        .build())
                .isExempted(false)
                .build();

        AviationReportableEmissionsDTO reportableEmissionsDTO2022 = AviationReportableEmissionsDTO.builder()
                .totalReportableEmissions(AviationAerUkEtsTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions2022)
                        .build())
                .isExempted(true)
                .build();

        when(aviationReportableEmissionsRepository
            .findAllByAccountIdAndYearIn(accountId, Set.of(year2021, year2022)))
            .thenReturn(List.of(reportableEmissionsEntity2021, reportableEmissionsEntity2022));

        // Invoke
        Map<Year, AviationReportableEmissionsDTO> reportableEmissionsResult =
            aviationReportableEmissionsService.getReportableEmissions(accountId, Set.of(year2021, year2022));

        // Verify
        assertThat(reportableEmissionsResult)
                .hasSize(2)
                .containsExactlyInAnyOrderEntriesOf(
                        Map.of(
                                year2021, reportableEmissionsDTO2021,
                                year2022, reportableEmissionsDTO2022
                        )
                );
        verify(aviationReportableEmissionsRepository, times(1))
                .findAllByAccountIdAndYearIn(accountId, Set.of(year2021, year2022));
    }

    @Test
    void getReportableEmissions_corsia() {
        Long accountId = 1L;
        Year year2021  = Year.of(2021);
        BigDecimal reportableEmissions2021 = BigDecimal.valueOf(305.90);
        BigDecimal reportableOffsetEmissions2021 = BigDecimal.valueOf(100);
        BigDecimal reportableReductionClaimEmissions2021 = BigDecimal.valueOf(205.90);
        Year year2022  = Year.of(2022);
        BigDecimal reportableEmissions2022 = BigDecimal.valueOf(335.90);
        BigDecimal reportableOffsetEmissions2022 = BigDecimal.valueOf(200);
        BigDecimal reportableReductionClaimEmissions2022 = BigDecimal.valueOf(135.90);

        AviationReportableEmissionsEntity reportableEmissionsEntity2021 = AviationReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year2021)
                .reportableEmissions(reportableEmissions2021)
                .reportableOffsetEmissions(reportableOffsetEmissions2021)
                .reportableReductionClaimEmissions(reportableReductionClaimEmissions2021)
                .build();

        AviationReportableEmissionsEntity reportableEmissionsEntity2022 = AviationReportableEmissionsEntity.builder()
                .accountId(accountId)
                .year(year2022)
                .reportableEmissions(reportableEmissions2022)
                .reportableOffsetEmissions(reportableOffsetEmissions2022)
                .reportableReductionClaimEmissions(reportableReductionClaimEmissions2022)
                .isExempted(true)
                .build();

        AviationReportableEmissionsDTO reportableEmissionsDTO2021 = AviationReportableEmissionsDTO.builder()
                .totalReportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions2021)
                        .reportableOffsetEmissions(reportableOffsetEmissions2021)
                        .reportableReductionClaimEmissions(reportableReductionClaimEmissions2021)
                        .build())
                .isExempted(false)
                .build();

        AviationReportableEmissionsDTO reportableEmissionsDTO2022 = AviationReportableEmissionsDTO.builder()
                .totalReportableEmissions(AviationAerCorsiaTotalReportableEmissions.builder()
                        .reportableEmissions(reportableEmissions2022)
                        .reportableOffsetEmissions(reportableOffsetEmissions2022)
                        .reportableReductionClaimEmissions(reportableReductionClaimEmissions2022)
                        .build())
                .isExempted(true)
                .build();

        when(aviationReportableEmissionsRepository
                .findAllByAccountIdAndYearIn(accountId, Set.of(year2021, year2022)))
                .thenReturn(List.of(reportableEmissionsEntity2021, reportableEmissionsEntity2022));

        // Invoke
        Map<Year, AviationReportableEmissionsDTO> reportableEmissionsResult =
                aviationReportableEmissionsService.getReportableEmissions(accountId, Set.of(year2021, year2022));

        // Verify
        assertThat(reportableEmissionsResult)
                .hasSize(2)
                .containsExactlyInAnyOrderEntriesOf(
                        Map.of(
                                year2021, reportableEmissionsDTO2021,
                                year2022, reportableEmissionsDTO2022
                        )
                );
        verify(aviationReportableEmissionsRepository, times(1))
                .findAllByAccountIdAndYearIn(accountId, Set.of(year2021, year2022));
    }

    @Test
    void getReportableEmissions_empty() {
        Long accountId = 1L;

        when(aviationReportableEmissionsRepository.findAllByAccountIdAndYearIn(accountId, Set.of(Year.of(2021))))
            .thenReturn(List.of());

        // Invoke
        Map<Year, AviationReportableEmissionsDTO> reportableEmissionsResult =
            aviationReportableEmissionsService.getReportableEmissions(accountId, Set.of(Year.of(2021)));

        // Verify
        assertThat(reportableEmissionsResult).isEmpty();
        verify(aviationReportableEmissionsRepository, times(1))
                .findAllByAccountIdAndYearIn(accountId, Set.of(Year.of(2021)));
    }

    @Test
    void updateReportableEmissions_corsia() {
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
                .reportingYear(year)
                .reportingRequired(true)
                .scheme(EmissionTradingScheme.CORSIA)
                .build();

        final AviationAerCorsiaTotalReportableEmissions reportableEmissions = AviationAerCorsiaTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(400))
                .reportableOffsetEmissions(BigDecimal.valueOf(300))
                .reportableReductionClaimEmissions(BigDecimal.valueOf(100))
                .build();
        final AviationReportableEmissionsSaveParams reportableEmissionsSaveParams =
                AviationReportableEmissionsSaveParams.builder()
                        .accountId(accountId)
                        .year(year)
                        .reportableEmissions(reportableEmissions)
                        .isFromDre(false)
                        .build();

        when(aviationAerCorsiaReportableEmissionsCalculationService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);
        when(aviationAerCorsiaReportableEmissionsCalculationService.calculateReportableEmissions(aerContainer))
                .thenReturn(reportableEmissions);
        when(aviationCorsiaReportableEmissionsUpdateService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);

        // Invoke
        AviationAerTotalReportableEmissions result = aviationReportableEmissionsService.updateReportableEmissions(aerContainer, accountId, false);

        // Verify
        assertThat(result)
                .isInstanceOf(AviationAerCorsiaTotalReportableEmissions.class)
                .isEqualTo(reportableEmissions);

        verify(aviationAerCorsiaReportableEmissionsCalculationService, times(1))
                .getEmissionTradingScheme();
        verify(aviationAerCorsiaReportableEmissionsCalculationService, times(1))
                .calculateReportableEmissions(aerContainer);
        verify(aviationCorsiaReportableEmissionsUpdateService, times(1))
                .getEmissionTradingScheme();
        verify(aviationCorsiaReportableEmissionsUpdateService, times(1))
                .saveReportableEmissions(reportableEmissionsSaveParams);
        verifyNoInteractions(aviationAerUkEtsReportableEmissionsCalculationService, aviationUkEtsReportableEmissionsUpdateService);
    }

    @Test
    void updateReportableEmissions_ukets() {
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingYear(year)
                .reportingRequired(true)
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        final AviationAerUkEtsTotalReportableEmissions reportableEmissions = AviationAerUkEtsTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(400))
                .build();
        final AviationReportableEmissionsSaveParams reportableEmissionsSaveParams =
                AviationReportableEmissionsSaveParams.builder()
                        .accountId(accountId)
                        .year(year)
                        .reportableEmissions(reportableEmissions)
                        .isFromDre(false)
                        .build();

        when(aviationAerCorsiaReportableEmissionsCalculationService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);
        when(aviationAerUkEtsReportableEmissionsCalculationService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        when(aviationAerUkEtsReportableEmissionsCalculationService.calculateReportableEmissions(aerContainer))
                .thenReturn(reportableEmissions);
        when(aviationCorsiaReportableEmissionsUpdateService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);
        when(aviationUkEtsReportableEmissionsUpdateService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);

        // Invoke
        AviationAerTotalReportableEmissions result = aviationReportableEmissionsService.updateReportableEmissions(aerContainer, accountId, false);

        // Verify
        assertThat(result)
                .isInstanceOf(AviationAerUkEtsTotalReportableEmissions.class)
                .isEqualTo(reportableEmissions);

        verify(aviationAerCorsiaReportableEmissionsCalculationService, times(1))
                .getEmissionTradingScheme();
        verify(aviationAerUkEtsReportableEmissionsCalculationService, times(1))
                .getEmissionTradingScheme();
        verify(aviationAerUkEtsReportableEmissionsCalculationService, times(1))
                .calculateReportableEmissions(aerContainer);
        verify(aviationCorsiaReportableEmissionsUpdateService, times(1))
                .getEmissionTradingScheme();
        verify(aviationUkEtsReportableEmissionsUpdateService, times(1))
                .getEmissionTradingScheme();
        verify(aviationUkEtsReportableEmissionsUpdateService, times(1))
                .saveReportableEmissions(reportableEmissionsSaveParams);
        verifyNoMoreInteractions(aviationAerCorsiaReportableEmissionsCalculationService, aviationCorsiaReportableEmissionsUpdateService);
    }

    @Test
    void updateReportableEmissions_no_reporting() {
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingYear(year)
                .reportingRequired(false)
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        // Invoke
        BusinessException be = assertThrows(BusinessException.class, () ->
            aviationReportableEmissionsService.updateReportableEmissions(aerContainer, accountId, false));

        // Verify
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Test
    void updateReportableEmissionsExemptedFlag() {
        Long accountId = 1L;
        Year year = Year.of(2023);
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(10L)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year))
                .thenReturn(Optional.of(reportableEmissionsEntity));

        assertFalse(reportableEmissionsEntity.isExempted());

        // Invoke
        aviationReportableEmissionsService.updateReportableEmissionsExemptedFlag(accountId, year, true);

        // Verify
        assertTrue(reportableEmissionsEntity.isExempted());
        verify(aviationReportableEmissionsRepository, times(1))
                .findByAccountIdAndYear(accountId, year);
    }

    @Test
    void deleteReportableEmissions() {
        Long accountId = 1L;
        Year year = Year.of(2023);
        AviationReportableEmissionsEntity reportableEmissionsEntity = AviationReportableEmissionsEntity.builder()
                .id(10L)
                .accountId(accountId)
                .year(year)
                .reportableEmissions(BigDecimal.valueOf(2000))
                .isFromDre(false)
                .build();

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year))
                .thenReturn(Optional.of(reportableEmissionsEntity));

        // Invoke
        aviationReportableEmissionsService.deleteReportableEmissions(accountId, year);

        // Verify
        verify(aviationReportableEmissionsRepository, times(1))
                .findByAccountIdAndYear(accountId, year);
        verify(aviationReportableEmissionsRepository, times(1))
                .delete(reportableEmissionsEntity);
    }

    @Test
    void deleteReportableEmissions_empty() {
        Long accountId = 1L;
        Year year = Year.of(2023);

        when(aviationReportableEmissionsRepository.findByAccountIdAndYear(accountId, year))
                .thenReturn(Optional.empty());

        // Invoke
        aviationReportableEmissionsService.deleteReportableEmissions(accountId, year);

        // Verify
        verify(aviationReportableEmissionsRepository, times(1))
                .findByAccountIdAndYear(accountId, year);
        verify(aviationReportableEmissionsRepository, never()).delete(any());
    }


    @Test
    void updateReportableEmissions_operator_submit_ukets() {
        final Long accountId = 1L;
        final Year year = Year.of(2022);
        final AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
                .reportingYear(year)
                .reportingRequired(true)
                .scheme(EmissionTradingScheme.UK_ETS_AVIATION)
                .build();

        final AviationAerUkEtsTotalReportableEmissions reportableEmissions = AviationAerUkEtsTotalReportableEmissions.builder()
                .reportableEmissions(BigDecimal.valueOf(400))
                .build();
        final AviationReportableEmissionsSaveParams reportableEmissionsSaveParams =
                AviationReportableEmissionsSaveParams.builder()
                        .accountId(accountId)
                        .year(year)
                        .reportableEmissions(reportableEmissions)
                        .isFromDre(false)
                        .build();

        when(aviationAerCorsiaReportableEmissionsCalculationService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);
        when(aviationAerUkEtsReportableEmissionsCalculationService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);
        when(aviationAerUkEtsReportableEmissionsCalculationService.calculateReportableEmissions(aerContainer))
                .thenReturn(reportableEmissions);
        when(aviationCorsiaReportableEmissionsUpdateService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.CORSIA);
        when(aviationUkEtsReportableEmissionsUpdateService.getEmissionTradingScheme())
                .thenReturn(EmissionTradingScheme.UK_ETS_AVIATION);

        AviationAerTotalReportableEmissions result = aviationReportableEmissionsService.updateReportableEmissions(aerContainer, accountId, false);

        assertThat(result)
                .isInstanceOf(AviationAerUkEtsTotalReportableEmissions.class)
                .isEqualTo(reportableEmissions);

        verify(aviationAerCorsiaReportableEmissionsCalculationService, times(1))
                .getEmissionTradingScheme();
        verify(aviationAerUkEtsReportableEmissionsCalculationService, times(1))
                .getEmissionTradingScheme();
        verify(aviationAerUkEtsReportableEmissionsCalculationService, times(1))
                .calculateReportableEmissions(aerContainer);
        verify(aviationCorsiaReportableEmissionsUpdateService, times(1))
                .getEmissionTradingScheme();
        verify(aviationUkEtsReportableEmissionsUpdateService, times(1))
                .getEmissionTradingScheme();
        verify(aviationUkEtsReportableEmissionsUpdateService, times(1))
                .saveReportableEmissions(reportableEmissionsSaveParams);
        verifyNoMoreInteractions(aviationAerCorsiaReportableEmissionsCalculationService, aviationCorsiaReportableEmissionsUpdateService);
    }
}
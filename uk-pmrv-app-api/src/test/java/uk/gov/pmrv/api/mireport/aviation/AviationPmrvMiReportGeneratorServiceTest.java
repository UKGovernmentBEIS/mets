package uk.gov.pmrv.api.mireport.aviation;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.MiReportRepository;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.netz.api.mireport.domain.MiReportResult;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AviationPmrvMiReportGeneratorServiceTest {
    private final EntityManager reportEaEntityManager = mock(EntityManager.class);
    private final MiReportRepository pmrvMiReportRepository = mock(MiReportRepository.class);
    private final AviationMiReportGeneratorHandler<EmptyMiReportParams> aviationMiReportGeneratorHandler = mock(AviationMiReportGeneratorHandler.class);
    private final List<AviationMiReportGeneratorHandler<EmptyMiReportParams>> handlers = Collections.singletonList(aviationMiReportGeneratorHandler);

    private final AviationPmrvMiReportGeneratorService<EmptyMiReportParams> service = new AviationPmrvMiReportGeneratorService<>(reportEaEntityManager,
            null, null, null, null, pmrvMiReportRepository, handlers);

    @BeforeEach
    void setup() {
        service.afterPropertiesSet();
    }

    @Test
    void generateReport() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();
        MiReportResult expectedMiReportResult = mock(MiReportResult.class);

        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(pmrvMiReportRepository.findByCompetentAuthority(competentAuthority))
            .thenReturn(List.of(expectedMiReportSearchResult));
        when(expectedMiReportSearchResult.getMiReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.getReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.generateMiReport(eq(reportEaEntityManager), eq(reportParams))).thenReturn(
            expectedMiReportResult);

        MiReportResult actualMiReportResult = service.generateReport(competentAuthority, reportParams);

        assertThat(actualMiReportResult).isEqualTo(expectedMiReportResult);
    }

    @Test
    void generateReport_entity_manager_not_found() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.SCOTLAND;
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();
        MiReportResult expectedMiReportResult = mock(MiReportResult.class);

        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(pmrvMiReportRepository.findByCompetentAuthority(competentAuthority))
                .thenReturn(List.of(expectedMiReportSearchResult));
        when(expectedMiReportSearchResult.getMiReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.getReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.generateMiReport(any(), eq(reportParams))).thenReturn(
                expectedMiReportResult);

        BusinessException businessException = assertThrows(
                BusinessException.class, () -> service.generateReport(competentAuthority, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());
        assertThat(businessException.getData()).contains(CompetentAuthorityEnum.SCOTLAND);
    }

    @Test
    void generateReport_generator_not_found() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(aviationMiReportGeneratorHandler.getReportType()).thenReturn(MiReportType.COMPLETED_WORK);

        BusinessException businessException = assertThrows(
            BusinessException.class, () -> service.generateReport(competentAuthority, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());

        verify(aviationMiReportGeneratorHandler, never()).generateMiReport(any(), any());
        verifyNoInteractions(pmrvMiReportRepository);
    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, service.getAccountType());
    }
}
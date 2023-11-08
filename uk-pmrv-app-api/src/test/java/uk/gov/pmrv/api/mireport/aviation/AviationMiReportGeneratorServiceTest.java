package uk.gov.pmrv.api.mireport.aviation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.mireport.common.MiReportRepository;
import uk.gov.pmrv.api.mireport.common.MiReportType;
import uk.gov.pmrv.api.mireport.common.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.common.domain.dto.MiReportSearchResult;

import java.util.ArrayList;
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

@ExtendWith(MockitoExtension.class)
class AviationMiReportGeneratorServiceTest {

    @InjectMocks
    private AviationMiReportGeneratorService service;

    @Mock
    private MiReportRepository miReportRepository;

    @Spy
    private ArrayList<AviationMiReportGeneratorHandler> handlers;

    @Mock
    private AviationMiReportGeneratorHandler<MiReportParams> aviationMiReportGeneratorHandler;

    @BeforeEach
    void setup() {
        handlers.add(aviationMiReportGeneratorHandler);
    }

    @Test
    void generateReport() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();
        MiReportResult expectedMiReportResult = mock(MiReportResult.class);

        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(miReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, service.getAccountType()))
            .thenReturn(List.of(expectedMiReportSearchResult));
        when(expectedMiReportSearchResult.getMiReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.getReportType()).thenReturn(reportType);
        when(aviationMiReportGeneratorHandler.generateMiReport(any(), eq(reportParams))).thenReturn(
            expectedMiReportResult);

        MiReportResult actualMiReportResult = service.generateReport(competentAuthority, reportParams);

        assertThat(actualMiReportResult).isEqualTo(expectedMiReportResult);
    }

    @Test
    void generateReport_generator_not_found() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        MiReportType reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();

        when(aviationMiReportGeneratorHandler.getReportType()).thenReturn(MiReportType.COMPLETED_WORK);

        BusinessException businessException = assertThrows(
            BusinessException.class, () -> service.generateReport(competentAuthority, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());

        verify(aviationMiReportGeneratorHandler, never()).generateMiReport(any(), any());
        verifyNoInteractions(miReportRepository);
    }

    @Test
    void getAccountType() {
        assertEquals(AccountType.AVIATION, service.getAccountType());
    }
}
package uk.gov.pmrv.api.mireport.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.netz.api.authorization.core.domain.AppAuthority;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.common.constants.RoleTypeConstants;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.netz.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.netz.api.mireport.MiReportType;
import uk.gov.netz.api.mireport.domain.EmptyMiReportParams;
import uk.gov.netz.api.mireport.domain.MiReportResult;
import uk.gov.netz.api.mireport.domain.MiReportSearchResult;
import uk.gov.netz.api.mireport.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.installation.InstallationPmrvMiReportGeneratorService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmrvMiReportServiceTest {

    @InjectMocks
    private PmrvMiReportService service;

    @Mock
    private PmrvMiReportRepository pmrvMiReportRepository;

    @Spy
    private ArrayList<PmrvMiReportGeneratorService> pmrvMiReportGeneratorServices;

    @Mock
    private InstallationPmrvMiReportGeneratorService installationMiReportGeneratorService;

    @BeforeEach
    void setup() {
        pmrvMiReportGeneratorServices.add(installationMiReportGeneratorService);
    }

    @Test
    void findByCompetentAuthorityAndAccountType() {
        CompetentAuthorityEnum competentAuthority = CompetentAuthorityEnum.ENGLAND;
        AccountType accountType = AccountType.INSTALLATION;
        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);

        when(pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType))
            .thenReturn(List.of(expectedMiReportSearchResult));

        List<MiReportSearchResult> actual = service.findByCompetentAuthorityAndAccountType(competentAuthority, accountType);

        assertThat(actual.get(0)).isEqualTo(expectedMiReportSearchResult);
    }

    @Test
    void generateReport() {
        AccountType accountType = AccountType.INSTALLATION;
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        String reportType = MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().reportType(reportType).build();
        MiReportResult expectedMiReportResult = mock(MiReportResult.class);

        MiReportSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSearchResult.class);
        when(expectedMiReportSearchResult.getMiReportType()).thenReturn(reportType);
        when(pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(CompetentAuthorityEnum.ENGLAND, accountType)).thenReturn(List.of(expectedMiReportSearchResult));

        when(installationMiReportGeneratorService.getAccountType()).thenReturn(accountType);
        when(installationMiReportGeneratorService.generateReport(appUser.getCompetentAuthority(), reportParams))
            .thenReturn(expectedMiReportResult);

        MiReportResult actualMiReportResult = service.generateReport(appUser.getCompetentAuthority(), accountType, reportParams);

        assertThat(actualMiReportResult).isEqualTo(expectedMiReportResult);
    }

    @Test
    void generateReport_generator_not_found() {
        AccountType accountType = AccountType.AVIATION;
        AppUser appUser = AppUser.builder()
                .userId("userId")
                .roleType(RoleTypeConstants.REGULATOR)
                .authorities(List.of(AppAuthority.builder()
                        .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                        .build()))
                .build();
        String reportType = MiReportType.COMPLETED_WORK;
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder().reportType(reportType).build();

        when(installationMiReportGeneratorService.getAccountType()).thenReturn(AccountType.INSTALLATION);

        BusinessException businessException = assertThrows(
            BusinessException.class, () -> service.generateReport(appUser.getCompetentAuthority(), accountType, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());
    }
}

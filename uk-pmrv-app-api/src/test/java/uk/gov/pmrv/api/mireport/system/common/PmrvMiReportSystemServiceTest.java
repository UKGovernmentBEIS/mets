package uk.gov.pmrv.api.mireport.system.common;

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
import uk.gov.netz.api.mireport.system.MiReportSystemType;
import uk.gov.netz.api.mireport.system.EmptyMiReportSystemParams;
import uk.gov.netz.api.mireport.system.MiReportSystemResult;
import uk.gov.netz.api.mireport.system.MiReportSystemSearchResult;
import uk.gov.netz.api.mireport.system.executedactions.ExecutedRequestActionsMiReportParams;
import uk.gov.pmrv.api.common.domain.enumeration.AccountType;
import uk.gov.pmrv.api.mireport.system.installation.InstallationPmrvMiReportGeneratorService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PmrvMiReportSystemServiceTest {

    @InjectMocks
    private PmrvMiReportSystemService service;

    @Mock
    private PmrvMiReportSystemRepository pmrvMiReportRepository;

    @Spy
    private ArrayList<PmrvMiReportSystemGeneratorService> pmrvMiReportGeneratorServices;

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
        MiReportSystemSearchResult expectedMiReportSearchResult = Mockito.mock(MiReportSystemSearchResult.class);

        when(pmrvMiReportRepository.findByCompetentAuthorityAndAccountType(competentAuthority, accountType))
            .thenReturn(List.of(expectedMiReportSearchResult));

        List<MiReportSystemSearchResult> actual = service.findByCompetentAuthorityAndAccountType(competentAuthority, accountType);

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
        String reportType = MiReportSystemType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
        EmptyMiReportSystemParams reportParams = EmptyMiReportSystemParams.builder().reportType(reportType).build();
        MiReportSystemResult expectedMiReportResult = mock(MiReportSystemResult.class);

		when(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(
				CompetentAuthorityEnum.ENGLAND, accountType, reportType)).thenReturn(true);

        when(installationMiReportGeneratorService.getAccountType()).thenReturn(accountType);
        when(installationMiReportGeneratorService.generateReport(appUser.getCompetentAuthority(), reportParams))
            .thenReturn(expectedMiReportResult);

        MiReportSystemResult actualMiReportResult = service.generateReport(appUser.getCompetentAuthority(), accountType, reportParams);

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
        String reportType = MiReportSystemType.COMPLETED_WORK;
        ExecutedRequestActionsMiReportParams reportParams = ExecutedRequestActionsMiReportParams.builder().reportType(reportType).build();

        when(pmrvMiReportRepository.existsByCompetentAuthorityAndAccountTypeAndMiReportType(
				CompetentAuthorityEnum.ENGLAND, accountType, reportType)).thenReturn(true);
        
        when(installationMiReportGeneratorService.getAccountType()).thenReturn(AccountType.INSTALLATION);

        BusinessException businessException = assertThrows(
            BusinessException.class, () -> service.generateReport(appUser.getCompetentAuthority(), accountType, reportParams));

        assertEquals(ErrorCode.MI_REPORT_TYPE_NOT_SUPPORTED, businessException.getErrorCode());
    }
}

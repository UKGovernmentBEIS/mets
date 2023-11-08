package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerEntity;
import uk.gov.pmrv.api.reporting.domain.AerSubmitParams;
import uk.gov.pmrv.api.reporting.domain.ReportableEmissionsSaveParams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.PermitOriginatedData;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivities;
import uk.gov.pmrv.api.reporting.domain.pollutantregistercodes.PollutantRegisterActivity;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.repository.AerRepository;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;

import java.math.BigDecimal;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerServiceTest {

    @InjectMocks
    private AerService aerService;

    @Mock
    private ReportableEmissionsService reportableEmissionsService;

    @Mock
    private ReportableEmissionsCalculationService reportableEmissionsCalculationService;

    @Mock
    private AerRepository aerRepository;

    @Mock
    private AerValidatorService aerValidatorService;

    @Test
    void submitAer() {
        Long accountId = 14567L;
        Year reportingYear = Year.parse("22/12/2020", DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        BigDecimal totalEmissions = BigDecimal.valueOf(1000);
        String aerId = "AEM14567-2020";
        Aer aer = Aer.builder()
                .pollutantRegisterActivities(PollutantRegisterActivities.builder()
                        .exist(true)
                        .activities(Set.of(PollutantRegisterActivity._1_A_2_A_IRON_AND_STEEL))
                        .build())
                .build();
        AerVerificationReport verificationReport = AerVerificationReport.builder().build();
        PermitOriginatedData permitOriginatedData = PermitOriginatedData.builder().build();
        AerContainer aerContainer = AerContainer.builder()
                .aer(aer)
                .reportingYear(reportingYear)
                .verificationReport(verificationReport)
                .permitOriginatedData(permitOriginatedData)
                .build();

        AerSubmitParams aerSubmitParams = AerSubmitParams.builder()
                .accountId(accountId)
                .aerContainer(aerContainer)
                .build();
        ReportableEmissionsSaveParams emissionsParams = ReportableEmissionsSaveParams.builder()
                .accountId(accountId)
                .year(reportingYear)
                .reportableEmissions(totalEmissions)
                .build();

        when(reportableEmissionsCalculationService.calculateYearEmissions(aerContainer))
                .thenReturn(totalEmissions);

        // Invoke
        BigDecimal actualEmissions = aerService.submitAer(aerSubmitParams);

        // Verify
        assertEquals(totalEmissions, actualEmissions);

        ArgumentCaptor<AerEntity> aerEntityArgumentCaptor = ArgumentCaptor.forClass(AerEntity.class);
        verify(aerRepository, times(1)).save(aerEntityArgumentCaptor.capture());
        AerEntity savedAerEntity = aerEntityArgumentCaptor.getValue();

        assertNotNull(savedAerEntity);
        assertEquals(aerId, savedAerEntity.getId());
        assertEquals(reportingYear, savedAerEntity.getYear());
        assertEquals(accountId, savedAerEntity.getAccountId());
        assertEquals(aerContainer, savedAerEntity.getAerContainer());

        verify(reportableEmissionsCalculationService, times(1))
                .calculateYearEmissions(aerContainer);
        verify(aerValidatorService, times(1))
                .validate(aerContainer, accountId);
        verify(reportableEmissionsService, times(1))
                .saveReportableEmissions(emissionsParams);
    }

    @Test
    void existsAerByAccountIdAndYear() {
        when(aerRepository.existsByAccountIdAndYear(1L, Year.of(2023))).thenReturn(true);

        assertTrue(aerService.existsAerByAccountIdAndYear(1L, Year.of(2023)));
    }
}
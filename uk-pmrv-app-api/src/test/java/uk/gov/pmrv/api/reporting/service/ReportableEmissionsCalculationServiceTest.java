package uk.gov.pmrv.api.reporting.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationData;
import uk.gov.pmrv.api.reporting.domain.verification.AerVerificationReport;
import uk.gov.pmrv.api.reporting.domain.verification.MonitoringApproachTypeEmissions;
import uk.gov.pmrv.api.reporting.domain.verification.OpinionStatement;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableAndBiomassEmission;
import uk.gov.pmrv.api.reporting.domain.verification.ReportableEmission;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation.CalculationApproachEmissionsCalculationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportableEmissionsCalculationServiceTest {

    @InjectMocks
    private ReportableEmissionsCalculationService reportableEmissionsCalculationService;

    @Spy
    private ArrayList<ApproachEmissionsCalculationService> approachEmissionsCalculationServices;

    @Mock
    private CalculationApproachEmissionsCalculationService calculationApproachEmissionsCalculationService;

    @BeforeEach
    void setUp() {
        approachEmissionsCalculationServices.add(calculationApproachEmissionsCalculationService);
    }

    @Test
    void calculateYearEmissions() {
        AerMonitoringApproachEmissions approachEmissions = CalculationOfCO2Emissions.builder().type(MonitoringApproachType.CALCULATION_CO2).build();
        AerContainer aerContainer = AerContainer.builder()
                .aer(Aer.builder()
                        .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, approachEmissions))
                                .build())
                        .build())
                .build();

        when(calculationApproachEmissionsCalculationService.getType())
                .thenReturn(MonitoringApproachType.CALCULATION_CO2);
        when(calculationApproachEmissionsCalculationService.getTotalEmissions(approachEmissions))
                .thenReturn(BigDecimal.valueOf(205.4));

        // Invoke
        BigDecimal actual = reportableEmissionsCalculationService.calculateYearEmissions(aerContainer);

        // Verify
        assertEquals(BigDecimal.valueOf(205.4).setScale(5, RoundingMode.HALF_UP), actual);
        verify(calculationApproachEmissionsCalculationService, times(1)).getType();
        verify(calculationApproachEmissionsCalculationService, times(1)).getTotalEmissions(approachEmissions);
    }

    @Test
    void calculateYearEmissions_with_verification_data() {
        AerContainer aerContainer = AerContainer.builder()
                .verificationReport(AerVerificationReport.builder()
                        .verificationData(AerVerificationData.builder()
                                .opinionStatement(OpinionStatement.builder()
                                        .operatorEmissionsAcceptable(false)
                                        .monitoringApproachTypeEmissions(
                                                MonitoringApproachTypeEmissions.builder()
                                                        .calculationCombustionEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .calculationProcessEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .calculationMassBalanceEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .calculationTransferredCO2Emissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .measurementCO2Emissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .measurementTransferredCO2Emissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .measurementN2OEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .measurementTransferredN2OEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .fallbackEmissions(ReportableAndBiomassEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .calculationPFCEmissions(ReportableEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .inherentCO2Emissions(ReportableEmission.builder()
                                                                .reportableEmissions(BigDecimal.TEN)
                                                                .build())
                                                        .build()
                                        )
                                        .build())
                                .build())
                        .build())
                .build();

        // Invoke
        BigDecimal actual = reportableEmissionsCalculationService.calculateYearEmissions(aerContainer);

        // Verify
        assertEquals(BigDecimal.valueOf(110.0).setScale(5, RoundingMode.HALF_UP), actual);
        verifyNoInteractions(calculationApproachEmissionsCalculationService);
    }

    @Test
    void calculateYearEmissions_with_verification_data_accepted_emissions() {
        AerMonitoringApproachEmissions approachEmissions = CalculationOfCO2Emissions.builder().type(MonitoringApproachType.CALCULATION_CO2).build();
        AerContainer aerContainer = AerContainer.builder()
                .verificationReport(AerVerificationReport.builder()
                        .verificationData(AerVerificationData.builder()
                                .opinionStatement(OpinionStatement.builder()
                                        .operatorEmissionsAcceptable(true)
                                        .build())
                                .build())
                        .build())
                .aer(Aer.builder()
                        .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                                .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, approachEmissions))
                                .build())
                        .build())
                .build();

        when(calculationApproachEmissionsCalculationService.getType())
                .thenReturn(MonitoringApproachType.CALCULATION_CO2);
        when(calculationApproachEmissionsCalculationService.getTotalEmissions(approachEmissions))
                .thenReturn(BigDecimal.valueOf(205.4));

        // Invoke
        BigDecimal actual = reportableEmissionsCalculationService.calculateYearEmissions(aerContainer);

        // Verify
        assertEquals(BigDecimal.valueOf(205.4).setScale(5, RoundingMode.HALF_UP), actual);
        verify(calculationApproachEmissionsCalculationService, times(1)).getType();
        verify(calculationApproachEmissionsCalculationService, times(1)).getTotalEmissions(approachEmissions);
    }
}

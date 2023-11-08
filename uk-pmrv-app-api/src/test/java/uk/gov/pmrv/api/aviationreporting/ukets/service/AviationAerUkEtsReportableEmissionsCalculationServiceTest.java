package uk.gov.pmrv.api.aviationreporting.ukets.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.ukets.aggregatedemissionsdata.AviationAerUkEtsFuelType;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEts;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsContainer;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.AviationAerUkEtsTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationData;
import uk.gov.pmrv.api.aviationreporting.ukets.domain.verification.AviationAerUkEtsVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.emissionsmonitoringplan.ukets.domain.emissionsmonitoringapproach.EmissionsMonitoringApproachType;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerUkEtsReportableEmissionsCalculationServiceTest {

    @InjectMocks
    private AviationAerUkEtsReportableEmissionsCalculationService reportableEmissionsCalculationService;

    @Mock
    private AviationAerUkEtsSubmittedEmissionsCalculationService emissionsCalculationService;

    @Test
    void calculateReportableEmissions() {
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .aer(aer)
            .build();

        BigDecimal expectedReportableEmissions = BigDecimal.valueOf(24500.98);

        when(emissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(expectedReportableEmissions);

        AviationAerUkEtsTotalReportableEmissions expectedTotalReportableEmissions =
            AviationAerUkEtsTotalReportableEmissions.builder().reportableEmissions(expectedReportableEmissions).build();

        AviationAerUkEtsTotalReportableEmissions actualTotalReportableEmissions =
            reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        assertEquals(expectedTotalReportableEmissions, actualTotalReportableEmissions);
    }

    @Test
    void calculateReportableEmissions_from_aer_object() {
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .aer(aer)
            .build();

        BigDecimal expectedReportableEmissions = BigDecimal.valueOf(24500.98);

        when(emissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(expectedReportableEmissions);

        AviationAerUkEtsTotalReportableEmissions expectedTotalReportableEmissions =
            AviationAerUkEtsTotalReportableEmissions.builder().reportableEmissions(expectedReportableEmissions).build();

        //invoke
        AviationAerUkEtsTotalReportableEmissions actualTotalReportableEmissions =
            reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        //verify
        assertEquals(expectedTotalReportableEmissions, actualTotalReportableEmissions);
    }

    @Test
    void calculateReportableEmissions_when_manually_provided_in_verification_report() {
        BigDecimal manuallyProvidedEmissions = BigDecimal.valueOf(15000);
        AviationAerUkEts aer = AviationAerUkEts.builder().build();
        AviationAerUkEtsVerificationReport verificationReport = AviationAerUkEtsVerificationReport.builder()
            .verificationData(AviationAerUkEtsVerificationData.builder()
                .opinionStatement(AviationAerOpinionStatement.builder()
                    .fuelTypes(Set.of(AviationAerUkEtsFuelType.JET_KEROSENE))
                    .monitoringApproachType(EmissionsMonitoringApproachType.FUEL_USE_MONITORING)
                    .emissionsCorrect(Boolean.FALSE)
                    .manuallyProvidedEmissions(manuallyProvidedEmissions)
                    .additionalChangesNotCovered(Boolean.FALSE)
                    .build())
                .build())
            .build();
        AviationAerUkEtsContainer aerContainer = AviationAerUkEtsContainer.builder()
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        AviationAerUkEtsTotalReportableEmissions expectedTotalReportableEmissions =
            AviationAerUkEtsTotalReportableEmissions.builder().reportableEmissions(manuallyProvidedEmissions).build();

        //invoke
        AviationAerUkEtsTotalReportableEmissions actualTotalReportableEmissions = reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        //verify
        assertEquals(expectedTotalReportableEmissions, actualTotalReportableEmissions);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.UK_ETS_AVIATION, reportableEmissionsCalculationService.getEmissionTradingScheme());
    }
}
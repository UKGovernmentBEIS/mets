package uk.gov.pmrv.api.aviationreporting.corsia.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsia;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaContainer;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.AviationAerCorsiaTotalReportableEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.totalemissions.AviationAerCorsiaTotalEmissions;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaOpinionStatement;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationData;
import uk.gov.pmrv.api.aviationreporting.corsia.domain.verification.AviationAerCorsiaVerificationReport;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AviationAerCorsiaReportableEmissionsCalculationServiceTest {

    @InjectMocks
    private AviationAerCorsiaReportableEmissionsCalculationService reportableEmissionsCalculationService;

    @Mock
    private AviationAerCorsiaSubmittedEmissionsCalculationService emissionsCalculationService;

    @Test
    void calculateReportableEmissions() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder().aer(aer).build();

        BigDecimal allFlightsEmissions = BigDecimal.valueOf(18660);
        BigDecimal offsetFlightsEmissions = BigDecimal.valueOf(12460);
        BigDecimal emissionsReductionClaim = BigDecimal.valueOf(123.45);

        AviationAerCorsiaTotalEmissions corsiaTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
            .allFlightsEmissions(allFlightsEmissions)
            .offsetFlightsEmissions(offsetFlightsEmissions)
            .emissionsReductionClaim(emissionsReductionClaim)
            .build();

        when(emissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(corsiaTotalEmissions);

        AviationAerCorsiaTotalReportableEmissions expected = AviationAerCorsiaTotalReportableEmissions.builder()
            .reportableEmissions(allFlightsEmissions)
            .reportableOffsetEmissions(offsetFlightsEmissions)
            .reportableReductionClaimEmissions(emissionsReductionClaim)
            .build();

        // Invoke
        AviationAerCorsiaTotalReportableEmissions result = reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void calculateReportableEmissions_no_emissions_reduction() {
        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder().aer(aer).build();

        BigDecimal allFlightsEmissions = BigDecimal.valueOf(18660);
        BigDecimal offsetFlightsEmissions = BigDecimal.valueOf(12460);

        AviationAerCorsiaTotalEmissions corsiaTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
            .allFlightsEmissions(allFlightsEmissions)
            .offsetFlightsEmissions(offsetFlightsEmissions)
            .emissionsReductionClaim(BigDecimal.ZERO)
            .build();

        when(emissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(corsiaTotalEmissions);

        AviationAerCorsiaTotalReportableEmissions expected = AviationAerCorsiaTotalReportableEmissions.builder()
            .reportableEmissions(allFlightsEmissions)
            .reportableOffsetEmissions(offsetFlightsEmissions)
            .reportableReductionClaimEmissions(BigDecimal.ZERO)
            .build();

        // Invoke
        AviationAerCorsiaTotalReportableEmissions result = reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void calculateReportableEmissions_with_verification_report() {
        BigDecimal allFlightsEmissions = BigDecimal.valueOf(18660);
        BigDecimal offsetFlightsEmissions = BigDecimal.valueOf(12460);
        BigDecimal emissionsReductionClaim = BigDecimal.valueOf(200);

        BigDecimal manuallyInternationalFlightsProvidedEmissions = BigDecimal.valueOf(1000);
        BigDecimal manuallyOffsettingFlightsProvidedEmissions = BigDecimal.valueOf(500);

        AviationAerCorsia aer = AviationAerCorsia.builder().build();
        AviationAerCorsiaVerificationReport verificationReport = AviationAerCorsiaVerificationReport.builder()
            .verificationData(AviationAerCorsiaVerificationData.builder()
                .opinionStatement(AviationAerCorsiaOpinionStatement.builder()
                    .emissionsCorrect(false)
                    .manuallyInternationalFlightsProvidedEmissions(BigDecimal.valueOf(1000))
                    .manuallyOffsettingFlightsProvidedEmissions(BigDecimal.valueOf(500))
                    .build())
                .build())
            .build();
        AviationAerCorsiaContainer aerContainer = AviationAerCorsiaContainer.builder()
            .aer(aer)
            .verificationReport(verificationReport)
            .build();

        AviationAerCorsiaTotalEmissions corsiaTotalEmissions = AviationAerCorsiaTotalEmissions.builder()
            .allFlightsEmissions(allFlightsEmissions)
            .offsetFlightsEmissions(offsetFlightsEmissions)
            .emissionsReductionClaim(emissionsReductionClaim)
            .build();

        when(emissionsCalculationService.calculateTotalSubmittedEmissions(aer)).thenReturn(corsiaTotalEmissions);

        AviationAerCorsiaTotalReportableEmissions expected = AviationAerCorsiaTotalReportableEmissions.builder()
            .reportableEmissions(manuallyInternationalFlightsProvidedEmissions)
            .reportableOffsetEmissions(manuallyOffsettingFlightsProvidedEmissions)
            .reportableReductionClaimEmissions(emissionsReductionClaim)
            .build();

        // Invoke
        AviationAerCorsiaTotalReportableEmissions result = reportableEmissionsCalculationService.calculateReportableEmissions(aerContainer);

        // Verify
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getEmissionTradingScheme() {
        assertEquals(EmissionTradingScheme.CORSIA, reportableEmissionsCalculationService.getEmissionTradingScheme());
    }
}
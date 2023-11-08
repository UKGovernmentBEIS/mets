package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.pfc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

public class PfcApproachEmissionsCalculationServiceTest {

    private final PfcApproachEmissionsCalculationService pfcApproachEmissionsCalculationService = new PfcApproachEmissionsCalculationService();

    @Test
    @DisplayName("should return the total emissions calculated when no manual values have been provided")
    void getTotalEmissionsWithCalculationCorrect() {
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .sourceStreamEmissions(List.of(
                PfcSourceStreamEmission.builder()
                    .reportableEmissions(BigDecimal.ONE)
                    .calculationCorrect(true)
                    .build()
            ))
            .build();

        BigDecimal totalEmissions = pfcApproachEmissionsCalculationService.getTotalEmissions(calculationOfPfcEmissions);
        assertEquals(BigDecimal.ONE, totalEmissions);
    }

    @Test
    @DisplayName("should return the manual provided emissions provided")
    void getTotalEmissionsWithCalculationIncorrect() {
        CalculationOfPfcEmissions calculationOfPfcEmissions = CalculationOfPfcEmissions.builder()
            .sourceStreamEmissions(List.of(
                PfcSourceStreamEmission.builder()
                    .reportableEmissions(BigDecimal.ONE)
                    .providedEmissions(PfcManuallyProvidedEmissions.builder()
                        .totalProvidedReportableEmissions(BigDecimal.TEN)
                        .reasonForProvidingManualEmissions("reason")
                        .build())
                    .calculationCorrect(false)
                    .build()
            ))
            .build();

        BigDecimal totalEmissions = pfcApproachEmissionsCalculationService.getTotalEmissions(calculationOfPfcEmissions);
        assertEquals(BigDecimal.TEN, totalEmissions);
    }

    @Test
    void getType() {
        assertEquals(pfcApproachEmissionsCalculationService.getType(), MonitoringApproachType.CALCULATION_PFC);
    }

}

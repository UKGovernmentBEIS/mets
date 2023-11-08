package uk.gov.pmrv.api.reporting.domain.verification;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OpinionStatementTest {

    @Test
    void reportable_emissions_sum_should_return_true() {
        OpinionStatement opinionStatement = OpinionStatement.builder()
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
                .build();

        final BigDecimal actual = opinionStatement.getMonitoringApproachTypeEmissions().getReportableEmissions();
        assertEquals(BigDecimal.valueOf(110L), actual);
    }
}

package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.n2o;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2ODirection;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OApproachEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MeasurementN2OApproachEmissionsCalculationServiceTest {

    private final MeasurementN2OApproachEmissionsCalculationService service =
        new MeasurementN2OApproachEmissionsCalculationService();

    @Test
    void getTotalEmissionsWithPositiveSignWithoutTransfer() {
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(
                MeasurementN2OEmissionPointEmission.builder()
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                    .build(),
                MeasurementN2OEmissionPointEmission.builder()
                    .calculationCorrect(Boolean.FALSE)
                    .reportableEmissions(BigDecimal.valueOf(52.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(53.10))
                    .providedEmissions(ManuallyProvidedEmissions.builder()
                        .totalProvidedReportableEmissions(BigDecimal.valueOf(11.10))
                        .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(43.10))
                        .build())
                    .build()))
            .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(62.2), actual);
    }

    @Test
    void getTotalEmissionsWithPositiveSignWithReceivingTransfer() {
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(
                MeasurementN2OEmissionPointEmission.builder()
                    .transfer(TransferN2O.builder().transferDirection(TransferN2ODirection.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(-51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(-49.10))
                    .build(),
                MeasurementN2OEmissionPointEmission.builder()
                    .transfer(TransferN2O.builder().transferDirection(TransferN2ODirection.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
                    .calculationCorrect(Boolean.FALSE)
                    .reportableEmissions(BigDecimal.valueOf(-52.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(-53.10))
                    .providedEmissions(ManuallyProvidedEmissions.builder()
                        .totalProvidedReportableEmissions(BigDecimal.valueOf(-11.10))
                        .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(-43.10))
                        .build())
                    .build()))
            .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(62.2), actual);
    }

    @Test
    void getTotalEmissionsWithNegativeSignForExporting() {
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(
                MeasurementN2OEmissionPointEmission.builder()
                    .transfer(TransferN2O.builder().transferDirection(TransferN2ODirection.EXPORTED_TO_LONG_TERM_FACILITY).build())
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                    .build(),
                MeasurementN2OEmissionPointEmission.builder()
                    .transfer(TransferN2O.builder().transferDirection(TransferN2ODirection.EXPORTED_TO_LONG_TERM_FACILITY).build())
                    .calculationCorrect(Boolean.FALSE)
                    .reportableEmissions(BigDecimal.valueOf(52.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(53.10))
                    .providedEmissions(ManuallyProvidedEmissions.builder()
                        .totalProvidedReportableEmissions(BigDecimal.valueOf(11.10))
                        .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(43.10))
                        .build())
                    .build()))
            .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(-62.2), actual);
    }

    @Test
    void getType() {
        assertEquals(MonitoringApproachType.MEASUREMENT_N2O, service.getType());
    }
}

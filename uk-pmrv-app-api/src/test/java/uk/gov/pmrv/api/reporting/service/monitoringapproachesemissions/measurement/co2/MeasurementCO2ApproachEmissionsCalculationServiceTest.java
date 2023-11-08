package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class MeasurementCO2ApproachEmissionsCalculationServiceTest {

    private final MeasurementCO2ApproachEmissionsCalculationService service =
        new MeasurementCO2ApproachEmissionsCalculationService();

    @Test
    void getTotalEmissionsWithPositiveSignWithoutTransfer() {
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(
                MeasurementCO2EmissionPointEmission.builder()
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                    .build(),
                MeasurementCO2EmissionPointEmission.builder()
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
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(
                MeasurementCO2EmissionPointEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(-51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(-49.10))
                    .build(),
                MeasurementCO2EmissionPointEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
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
        AerMonitoringApproachEmissions approachEmissions = MeasurementOfCO2Emissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointEmissions(List.of(
                MeasurementCO2EmissionPointEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM).build())
                    .calculationCorrect(Boolean.TRUE)
                    .reportableEmissions(BigDecimal.valueOf(51.10))
                    .sustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                    .build(),
                MeasurementCO2EmissionPointEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.EXPORTED_TO_LONG_TERM_FACILITY).build())
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
        assertEquals(MonitoringApproachType.MEASUREMENT_CO2, service.getType());
    }
}

package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2Direction;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNationalInventoryDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CalculationApproachEmissionsCalculationServiceTest {

    @InjectMocks
    private CalculationApproachEmissionsCalculationService service;

    @Test
    void getTotalEmissionsWithPositiveSignWithoutTransfer() {
        AerMonitoringApproachEmissions approachEmissions = CalculationOfCO2Emissions.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .sourceStreamEmissions(List.of(
                        CalculationSourceStreamEmission.builder()
                                .parameterCalculationMethod(CalculationManualCalculationMethod.builder()
                                        .type(CalculationParameterCalculationMethodType.MANUAL)
                                        .emissionCalculationParamValues(CalculationManualEmissionCalculationParamValues.builder()
                                                .calculationCorrect(Boolean.TRUE)
                                                .totalReportableEmissions(BigDecimal.valueOf(51.10))
                                                .totalSustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                                                .build())
                                        .build())
                                .build(),
                        CalculationSourceStreamEmission.builder()
                                .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                                        .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                                        .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder()
                                                .calculationCorrect(Boolean.FALSE)
                                                .totalReportableEmissions(BigDecimal.valueOf(52.10))
                                                .totalSustainableBiomassEmissions(BigDecimal.valueOf(53.10))
                                                .providedEmissions(ManuallyProvidedEmissions.builder()
                                                        .totalProvidedReportableEmissions(BigDecimal.valueOf(11.10))
                                                        .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(43.10))
                                                        .build())
                                                .build())
                                        .build())
                                .build()
                ))
                .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(62.2), actual);
    }

    @Test
    void getTotalEmissionsWithPositiveSignWithReceivingTransfer() {
        AerMonitoringApproachEmissions approachEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(
                CalculationSourceStreamEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
                    .parameterCalculationMethod(CalculationManualCalculationMethod.builder()
                        .type(CalculationParameterCalculationMethodType.MANUAL)
                        .emissionCalculationParamValues(CalculationManualEmissionCalculationParamValues.builder()
                            .calculationCorrect(Boolean.TRUE)
                            .totalReportableEmissions(BigDecimal.valueOf(-51.10))
                            .totalSustainableBiomassEmissions(BigDecimal.valueOf(-49.10))
                            .build())
                        .build())
                    .build(),
                CalculationSourceStreamEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.RECEIVED_FROM_ANOTHER_INSTALLATION).build())
                    .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                        .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                        .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder()
                            .calculationCorrect(Boolean.FALSE)
                            .totalReportableEmissions(BigDecimal.valueOf(-52.10))
                            .totalSustainableBiomassEmissions(BigDecimal.valueOf(-53.10))
                            .providedEmissions(ManuallyProvidedEmissions.builder()
                                .totalProvidedReportableEmissions(BigDecimal.valueOf(-11.10))
                                .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(-43.10))
                                .build())
                            .build())
                        .build())
                    .build()
            ))
            .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(62.2), actual);
    }

    @Test
    void getTotalEmissionsWithNegativeSignForExporting() {
        AerMonitoringApproachEmissions approachEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(
                CalculationSourceStreamEmission.builder()
                    .transfer(TransferCO2.builder()
                        .transferDirection(TransferCO2Direction.EXPORTED_FOR_PRECIPITATED_CALCIUM)
                        .build())
                    .parameterCalculationMethod(CalculationManualCalculationMethod.builder()
                        .type(CalculationParameterCalculationMethodType.MANUAL)
                        .emissionCalculationParamValues(CalculationManualEmissionCalculationParamValues.builder()
                            .calculationCorrect(Boolean.TRUE)
                            .totalReportableEmissions(BigDecimal.valueOf(51.10))
                            .totalSustainableBiomassEmissions(BigDecimal.valueOf(49.10))
                            .build())
                        .build())
                    .build(),
                CalculationSourceStreamEmission.builder()
                    .transfer(TransferCO2.builder().transferDirection(TransferCO2Direction.EXPORTED_TO_LONG_TERM_FACILITY).build())
                    .parameterCalculationMethod(CalculationNationalInventoryDataCalculationMethod.builder()
                        .type(CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA)
                        .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder()
                            .calculationCorrect(Boolean.FALSE)
                            .totalReportableEmissions(BigDecimal.valueOf(52.10))
                            .totalSustainableBiomassEmissions(BigDecimal.valueOf(53.10))
                            .providedEmissions(ManuallyProvidedEmissions.builder()
                                .totalProvidedReportableEmissions(BigDecimal.valueOf(11.10))
                                .totalProvidedSustainableBiomassEmissions(BigDecimal.valueOf(43.10))
                                .build())
                            .build())
                        .build())
                    .build()
            ))
            .build();

        // Invoke
        BigDecimal actual = service.getTotalEmissions(approachEmissions);

        // Verify
        assertEquals(BigDecimal.valueOf(-62.2), actual);
    }

    @Test
    void getType() {
        assertEquals(MonitoringApproachType.CALCULATION_CO2, service.getType());
    }
}

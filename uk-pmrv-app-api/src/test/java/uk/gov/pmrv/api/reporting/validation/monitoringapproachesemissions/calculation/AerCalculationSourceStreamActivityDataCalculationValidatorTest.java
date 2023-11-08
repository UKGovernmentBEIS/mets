package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataAggregationMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataContinuousMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationSourceStreamActivityDataCalculationValidatorTest {

    private AerCalculationSourceStreamActivityDataCalculationValidator validator;
    private AerCalculationSourceStreamActivityDataConditionCalculation validator1;
    private AerCalculationSourceStreamActivityDataConditionCalculation validator2;


    @BeforeEach
    void setUp() {
        validator1 = mock(AerCalculationSourceStreamActivityDataConditionCalculation.class);
        validator2 = mock(AerCalculationSourceStreamActivityDataConditionCalculation.class);
        validator = new AerCalculationSourceStreamActivityDataCalculationValidator(List.of(validator1, validator2));
    }

    @Test
    void validate_valid_manual() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .type(CalculationActivityDataCalculationMethodType.AGGREGATION_OF_METERING_QUANTITIES)
                                .materialOpeningQuantity(BigDecimal.valueOf(120.10))
                                .materialClosingQuantity(BigDecimal.valueOf(20.10))
                                .materialImportedOrExported(true)
                                .materialImportedQuantity(BigDecimal.valueOf(10.20))
                                .materialExportedQuantity(BigDecimal.valueOf(30.30))
                                .totalMaterial(BigDecimal.valueOf(79.9000))
                                .activityData(BigDecimal.valueOf(79.9000))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();
        AerContainer aerContainer = AerContainer.builder().build();

        when(validator1.validateActivityData(sourceStreamEmission)).thenReturn(List.of());
        when(validator1.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA));

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, aerContainer);
        verify(validator1, times(1)).validateActivityData(sourceStreamEmission);
        verify(validator2, never()).validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_valid_regional() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .type(CalculationActivityDataCalculationMethodType.AGGREGATION_OF_METERING_QUANTITIES)
                                .materialOpeningQuantity(BigDecimal.valueOf(120.10))
                                .materialClosingQuantity(BigDecimal.valueOf(20.10))
                                .materialImportedOrExported(true)
                                .materialImportedQuantity(BigDecimal.valueOf(10.20))
                                .materialExportedQuantity(BigDecimal.valueOf(30.30))
                                .totalMaterial(BigDecimal.valueOf(79.9000))
                                .activityData(BigDecimal.valueOf(79.9000))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();
        AerContainer aerContainer = AerContainer.builder().build();

        when(validator2.validateActivityData(sourceStreamEmission)).thenReturn(List.of());
        when(validator1.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA));
        when(validator2.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.REGIONAL_DATA));

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, aerContainer);
        verify(validator2, times(1)).validateActivityData(sourceStreamEmission);
        verify(validator1, never()).validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validate_valid_no_aggregation_of_metering_quantities_calc_method() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataContinuousMeteringCalcMethod.builder()
                                .type(CalculationActivityDataCalculationMethodType.CONTINUOUS_METERING)
                                .totalMaterial(BigDecimal.valueOf(79.90))
                                .activityData(BigDecimal.valueOf(79.9000))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();
        AerContainer aerContainer = AerContainer.builder().build();
        when(validator1.validateActivityData(sourceStreamEmission)).thenReturn(List.of());
        when(validator1.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA));

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validateTotalMaterial_invalid() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .type(CalculationActivityDataCalculationMethodType.AGGREGATION_OF_METERING_QUANTITIES)
                                .materialOpeningQuantity(BigDecimal.valueOf(120.00))
                                .materialClosingQuantity(BigDecimal.valueOf(-20.00))
                                .materialImportedOrExported(true)
                                .materialImportedQuantity(BigDecimal.valueOf(25.50))
                                .materialExportedQuantity(BigDecimal.valueOf(30.450))
                                .totalMaterial(BigDecimal.valueOf(135.0500001))
                                .activityData(BigDecimal.valueOf(135.0500001))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();
        AerContainer aerContainer = AerContainer.builder().build();
        when(validator1.validateActivityData(sourceStreamEmission)).thenReturn(List.of());
        when(validator1.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA));

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INCORRECT_TOTAL_MATERIAL.getMessage(), violation.getMessage());
    }

    @Test
    void validateActivityData_invalid() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .type(CalculationActivityDataCalculationMethodType.AGGREGATION_OF_METERING_QUANTITIES)
                                .materialOpeningQuantity(BigDecimal.valueOf(120.00))
                                .materialClosingQuantity(BigDecimal.valueOf(-20.00))
                                .materialImportedOrExported(true)
                                .materialImportedQuantity(BigDecimal.valueOf(25.50))
                                .materialExportedQuantity(BigDecimal.valueOf(30.450))
                                .totalMaterial(BigDecimal.valueOf(135.05))
                                .activityData(BigDecimal.valueOf(135.123))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();
        AerContainer aerContainer = AerContainer.builder().build();
        when(validator1.validateActivityData(sourceStreamEmission)).thenReturn(List.of(new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA,
                sourceStreamEmission.getSourceStream())));
        when(validator1.getTypes()).thenReturn(List.of(CalculationParameterCalculationMethodType.MANUAL, CalculationParameterCalculationMethodType.NATIONAL_INVENTORY_DATA));

        List<AerViolation> aerViolations = validator.validate(sourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA.getMessage(), violation.getMessage());
    }

}
package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataAggregationMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationFuelMeteringConditionType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationInventoryEmissionCalculationParamValues;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationRegionalDataCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerCalculationSourceStreamActivityDataConditionCalculationValidatorRegionalTest {
    private final AerCalculationSourceStreamActivityDataConditionCalculationValidatorRegional validator =
            new AerCalculationSourceStreamActivityDataConditionCalculationValidatorRegional();

    @Test
    void validateActivityData_valid_0degrees() {
        CalculationRegionalDataCalculationMethod manualCalculationMethod = CalculationRegionalDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .fuelMeteringConditionType(CalculationFuelMeteringConditionType.CELSIUS_0)
                .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder().calculationFactor(BigDecimal.valueOf(0.9476)).build())
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(135.05))
                                .activityData(BigDecimal.valueOf(135.05))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();

        List<AerViolation> aerViolations = validator.validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isEmpty();
    }
    @Test
    void validateActivityData_invalid_0degrees() {
        CalculationRegionalDataCalculationMethod manualCalculationMethod = CalculationRegionalDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .fuelMeteringConditionType(CalculationFuelMeteringConditionType.CELSIUS_0)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(135.05))
                                .activityData(BigDecimal.valueOf(135.123))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();

        List<AerViolation> aerViolations = validator.validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA.getMessage(), violation.getMessage());
    }

    @Test
    void validateActivityData_valid_15degrees() {
        CalculationRegionalDataCalculationMethod manualCalculationMethod = CalculationRegionalDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .fuelMeteringConditionType(CalculationFuelMeteringConditionType.CELSIUS_15)
                .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder().calculationFactor(BigDecimal.valueOf(0.9476)).build())
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(135.05))
                                .activityData(BigDecimal.valueOf(127.97338))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();

        List<AerViolation> aerViolations = validator.validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isEmpty();
    }

    @Test
    void validateActivityData_invalid_15degrees() {
        CalculationRegionalDataCalculationMethod manualCalculationMethod = CalculationRegionalDataCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.REGIONAL_DATA)
                .fuelMeteringConditionType(CalculationFuelMeteringConditionType.CELSIUS_15)
                .emissionCalculationParamValues(CalculationInventoryEmissionCalculationParamValues.builder().calculationFactor(BigDecimal.valueOf(0.9476)).build())
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(135.05))
                                .activityData(BigDecimal.valueOf(127.123))
                                .build()
                )
                .build();
        CalculationSourceStreamEmission sourceStreamEmission = CalculationSourceStreamEmission.builder()
                .parameterCalculationMethod(manualCalculationMethod)
                .build();

        List<AerViolation> aerViolations = validator.validateActivityData(sourceStreamEmission);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(1, aerViolations.size());

        AerViolation violation = aerViolations.get(0);
        assertEquals("CalculationOfCO2Emissions", violation.getSectionName());
        assertEquals(AerViolation.AerViolationMessage.CALCULATION_INCORRECT_ACTIVITY_DATA.getMessage(), violation.getMessage());
    }
}
package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataAggregationMeteringCalcMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationManualCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterCalculationMethodType;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AerCalculationSourceStreamActivityDataConditionCalculationValidatorDefaultTest {

    private final AerCalculationSourceStreamActivityDataConditionCalculationValidatorDefault validator =
            new AerCalculationSourceStreamActivityDataConditionCalculationValidatorDefault();

    @Test
    void validateActivityData_valid() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(79.9000))
                                .activityData(BigDecimal.valueOf(79.9000))
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
    void validateActivityData_invalid() {
        CalculationManualCalculationMethod manualCalculationMethod = CalculationManualCalculationMethod.builder()
                .type(CalculationParameterCalculationMethodType.MANUAL)
                .calculationActivityDataCalculationMethod(
                        CalculationActivityDataAggregationMeteringCalcMethod.builder()
                                .totalMaterial(BigDecimal.valueOf(135.0500001))
                                .activityData(BigDecimal.valueOf(135.05))
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
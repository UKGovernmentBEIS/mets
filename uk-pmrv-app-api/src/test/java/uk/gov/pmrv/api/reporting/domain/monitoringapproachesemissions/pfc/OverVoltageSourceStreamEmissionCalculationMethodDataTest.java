package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class OverVoltageSourceStreamEmissionCalculationMethodDataTest {

    @Test
    void should_be_equal() {
        OverVoltageSourceStreamEmissionCalculationMethodData expected =
            OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                .overVoltageCoefficient(BigDecimal.ONE)
                .anodeEffectsOverVoltagePerCell(BigDecimal.ONE)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build();

        OverVoltageSourceStreamEmissionCalculationMethodData actual =
            OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                .overVoltageCoefficient(BigDecimal.ONE)
                .anodeEffectsOverVoltagePerCell(BigDecimal.ONE)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        OverVoltageSourceStreamEmissionCalculationMethodData expected =
            OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                .overVoltageCoefficient(BigDecimal.ONE)
                .anodeEffectsOverVoltagePerCell(BigDecimal.ONE)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build();

        OverVoltageSourceStreamEmissionCalculationMethodData actual =
            OverVoltageSourceStreamEmissionCalculationMethodData.builder()
                .calculationMethod(PFCCalculationMethod.OVERVOLTAGE)
                .overVoltageCoefficient(BigDecimal.TEN)
                .anodeEffectsOverVoltagePerCell(BigDecimal.ONE)
                .aluminiumAverageCurrentEfficiencyProduction(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }
}
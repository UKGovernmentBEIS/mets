package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SlopeSourceStreamEmissionCalculationMethodDataTest {

    @Test
    void should_be_equal() {
        SlopeSourceStreamEmissionCalculationMethodData expected = SlopeSourceStreamEmissionCalculationMethodData.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .c2F6WeightFraction(BigDecimal.ONE)
            .slopeCF4EmissionFactor(BigDecimal.ONE)
            .anodeEffectsPerCellDay(BigDecimal.ONE)
            .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
            .percentageOfCollectionEfficiency(BigDecimal.ONE)
            .build();

        SlopeSourceStreamEmissionCalculationMethodData actual = SlopeSourceStreamEmissionCalculationMethodData.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .c2F6WeightFraction(BigDecimal.ONE)
            .slopeCF4EmissionFactor(BigDecimal.ONE)
            .anodeEffectsPerCellDay(BigDecimal.ONE)
            .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
            .percentageOfCollectionEfficiency(BigDecimal.ONE)
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        SlopeSourceStreamEmissionCalculationMethodData expected = SlopeSourceStreamEmissionCalculationMethodData.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .c2F6WeightFraction(BigDecimal.ONE)
            .slopeCF4EmissionFactor(BigDecimal.ONE)
            .anodeEffectsPerCellDay(BigDecimal.ONE)
            .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
            .percentageOfCollectionEfficiency(BigDecimal.ONE)
            .build();

        SlopeSourceStreamEmissionCalculationMethodData actual = SlopeSourceStreamEmissionCalculationMethodData.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .c2F6WeightFraction(BigDecimal.TEN)
            .slopeCF4EmissionFactor(BigDecimal.ONE)
            .anodeEffectsPerCellDay(BigDecimal.ONE)
            .averageDurationOfAnodeEffectsInMinutes(BigDecimal.ONE)
            .percentageOfCollectionEfficiency(BigDecimal.ONE)
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }


}
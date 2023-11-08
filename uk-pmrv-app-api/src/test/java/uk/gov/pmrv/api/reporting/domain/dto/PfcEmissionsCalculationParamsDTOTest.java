package uk.gov.pmrv.api.reporting.domain.dto;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCCalculationMethod;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.SlopeSourceStreamEmissionCalculationMethodData;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PfcEmissionsCalculationParamsDTOTest {

    @Test
    void should_be_equal() {
        PfcEmissionsCalculationParamsDTO expected = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build())
            .totalPrimaryAluminium(BigDecimal.ONE)
            .build();

        PfcEmissionsCalculationParamsDTO actual = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build())
            .totalPrimaryAluminium(BigDecimal.ONE)
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        PfcEmissionsCalculationParamsDTO expected = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.ONE)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build())
            .totalPrimaryAluminium(BigDecimal.ONE)
            .build();

        PfcEmissionsCalculationParamsDTO actual = PfcEmissionsCalculationParamsDTO.builder()
            .calculationMethod(PFCCalculationMethod.SLOPE)
            .pfcSourceStreamEmissionCalculationMethodData(SlopeSourceStreamEmissionCalculationMethodData.builder()
                .percentageOfCollectionEfficiency(BigDecimal.TEN)
                .calculationMethod(PFCCalculationMethod.SLOPE)
                .slopeCF4EmissionFactor(BigDecimal.ONE)
                .anodeEffectsPerCellDay(BigDecimal.ONE)
                .c2F6WeightFraction(BigDecimal.TEN)
                .percentageOfCollectionEfficiency(BigDecimal.ONE)
                .build())
            .totalPrimaryAluminium(BigDecimal.ONE)
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

}
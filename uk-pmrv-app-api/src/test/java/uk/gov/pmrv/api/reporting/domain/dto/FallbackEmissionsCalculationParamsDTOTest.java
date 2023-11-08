package uk.gov.pmrv.api.reporting.domain.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FallbackEmissionsCalculationParamsDTOTest {

    @Test
    void should_be_equal() {
        FallbackEmissionsCalculationParamsDTO expected = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(true)
            .totalFossilEmissions(BigDecimal.TEN)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        FallbackEmissionsCalculationParamsDTO actual = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(true)
            .totalFossilEmissions(BigDecimal.TEN)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        FallbackEmissionsCalculationParamsDTO expected = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(true)
            .totalFossilEmissions(BigDecimal.TEN)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        FallbackEmissionsCalculationParamsDTO actual = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(false)
            .totalFossilEmissions(BigDecimal.TEN)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

}
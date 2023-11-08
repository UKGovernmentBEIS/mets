package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FallbackBiomassTest {

    @Test
    void should_be_equal() {
        FallbackBiomass expected = FallbackBiomass.builder()
            .contains(true)
            .totalEnergyContentFromBiomass(BigDecimal.ONE)
            .totalSustainableBiomassEmissions(BigDecimal.ONE)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        FallbackBiomass actual = FallbackBiomass.builder()
            .contains(true)
            .totalEnergyContentFromBiomass(BigDecimal.ONE)
            .totalSustainableBiomassEmissions(BigDecimal.ONE)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        FallbackBiomass expected = FallbackBiomass.builder()
            .contains(true)
            .totalEnergyContentFromBiomass(BigDecimal.ONE)
            .totalSustainableBiomassEmissions(BigDecimal.ONE)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        FallbackBiomass actual = FallbackBiomass.builder()
            .contains(false)
            .totalEnergyContentFromBiomass(BigDecimal.ONE)
            .totalSustainableBiomassEmissions(BigDecimal.ONE)
            .totalNonSustainableBiomassEmissions(BigDecimal.ONE)
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }
}
package uk.gov.pmrv.api.reporting.domain.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PfcEmissionsCalculationDTOTest {

    @Test
    void should_be_equal() {
        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .amountOfC2F6(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .build();

        PfcEmissionsCalculationDTO actual = PfcEmissionsCalculationDTO.builder()
            .amountOfC2F6(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        PfcEmissionsCalculationDTO expected = PfcEmissionsCalculationDTO.builder()
            .amountOfC2F6(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .build();

        PfcEmissionsCalculationDTO actual = PfcEmissionsCalculationDTO.builder()
            .amountOfC2F6(BigDecimal.ONE)
            .totalC2F6Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.ONE)
            .totalCF4Emissions(BigDecimal.ONE)
            .reportableEmissions(BigDecimal.TEN)
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

}
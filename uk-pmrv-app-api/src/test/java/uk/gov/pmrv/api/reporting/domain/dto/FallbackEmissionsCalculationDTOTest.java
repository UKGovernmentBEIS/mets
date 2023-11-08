package uk.gov.pmrv.api.reporting.domain.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class FallbackEmissionsCalculationDTOTest {

    @Test
    void should_be_equal() {
        FallbackEmissionsCalculationDTO expected = FallbackEmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.ONE)
            .build();

        FallbackEmissionsCalculationDTO actual =
            FallbackEmissionsCalculationDTO.builder().reportableEmissions(BigDecimal.ONE).build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        FallbackEmissionsCalculationDTO expected = FallbackEmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.ONE)
            .build();

        FallbackEmissionsCalculationDTO actual =
            FallbackEmissionsCalculationDTO.builder().reportableEmissions(BigDecimal.TEN).build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

}
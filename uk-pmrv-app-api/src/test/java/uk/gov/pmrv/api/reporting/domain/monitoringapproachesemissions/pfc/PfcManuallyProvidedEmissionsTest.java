package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PfcManuallyProvidedEmissionsTest {

    @Test
    void should_be_equal() {
        PfcManuallyProvidedEmissions expected = PfcManuallyProvidedEmissions.builder()
            .reasonForProvidingManualEmissions("reason")
            .totalProvidedReportableEmissions(BigDecimal.ONE)
            .build();

        PfcManuallyProvidedEmissions actual = PfcManuallyProvidedEmissions.builder()
            .totalProvidedReportableEmissions(BigDecimal.ONE)
            .reasonForProvidingManualEmissions("reason")
            .build();

        assertEquals(expected, actual);
        assertEquals(expected.hashCode(), actual.hashCode());
    }

    @Test
    void should_not_be_equal() {
        PfcManuallyProvidedEmissions expected = PfcManuallyProvidedEmissions.builder()
            .reasonForProvidingManualEmissions("reason")
            .totalProvidedReportableEmissions(BigDecimal.ONE)
            .build();

        PfcManuallyProvidedEmissions actual = PfcManuallyProvidedEmissions.builder()
            .totalProvidedReportableEmissions(BigDecimal.ONE)
            .reasonForProvidingManualEmissions("another reason")
            .build();

        assertNotEquals(expected, actual);
        assertNotEquals(expected.hashCode(), actual.hashCode());
    }

}
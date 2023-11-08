package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.fallback;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackBiomass;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FallbackApproachEmissionsCalculationServiceTest {

    private final FallbackApproachEmissionsCalculationService service =
        new FallbackApproachEmissionsCalculationService();

    @Test
    void getType() {
        assertEquals(MonitoringApproachType.FALLBACK, service.getType());
    }

    @Test
    void getTotalEmissionsWithoutBiomass() {
        FallbackEmissions fallbackEmissions = FallbackEmissions.builder()
            .type(MonitoringApproachType.FALLBACK)
            .totalFossilEmissions(BigDecimal.valueOf(100))
            .biomass(FallbackBiomass.builder()
                .contains(false)
                .build())
            .build();

        BigDecimal totalEmissions = service.getTotalEmissions(fallbackEmissions);

        assertEquals(BigDecimal.valueOf(100), totalEmissions);
    }

    @Test
    void getTotalEmissionsWithBiomass() {
        FallbackEmissions fallbackEmissions = FallbackEmissions.builder()
            .type(MonitoringApproachType.FALLBACK)
            .totalFossilEmissions(BigDecimal.valueOf(100))
            .biomass(FallbackBiomass.builder()
                .contains(true)
                .totalNonSustainableBiomassEmissions(BigDecimal.valueOf(50))
                .build())
            .build();

        BigDecimal totalEmissions = service.getTotalEmissions(fallbackEmissions);

        assertEquals(BigDecimal.valueOf(150), totalEmissions);
    }

    @Test
    void calculateEmissionsWithoutBiomass() {
        FallbackEmissionsCalculationParamsDTO params = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(false)
            .totalFossilEmissions(BigDecimal.valueOf(100))
            .totalNonSustainableBiomassEmissions(BigDecimal.valueOf(50))
            .build();

        FallbackEmissionsCalculationDTO emissions = service.calculateEmissions(params);

        assertEquals(BigDecimal.valueOf(100), emissions.getReportableEmissions());
    }

    @Test
    void calculateEmissionsWithBiomass() {
        FallbackEmissionsCalculationParamsDTO params = FallbackEmissionsCalculationParamsDTO.builder()
            .containsBiomass(true)
            .totalFossilEmissions(BigDecimal.valueOf(100))
            .totalNonSustainableBiomassEmissions(BigDecimal.valueOf(50))
            .build();

        FallbackEmissionsCalculationDTO emissions = service.calculateEmissions(params);

        assertEquals(BigDecimal.valueOf(150), emissions.getReportableEmissions());
    }
}
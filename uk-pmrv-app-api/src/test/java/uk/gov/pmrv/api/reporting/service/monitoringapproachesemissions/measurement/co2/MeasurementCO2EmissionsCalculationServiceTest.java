package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.co2;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.MeasurementEmissionsCalculationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeasurementCO2EmissionsCalculationServiceTest {

    private MeasurementCO2EmissionsCalculationService measurementCO2EmissionsCalculationService =
        new MeasurementCO2EmissionsCalculationService(new MeasurementEmissionsCalculationService());

    @Test
    void calculateEmissionsWithBiomass() {
        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .containsBiomass(true)
                .biomassPercentage(new BigDecimal("25.00"))
                .operationalHours(BigDecimal.TEN)
                .annualHourlyAverageFlueGasFlow(BigDecimal.TEN)
                .annualHourlyAverageGHGConcentration(BigDecimal.valueOf(20))
                .build();

        MeasurementEmissionsCalculationDTO expected = MeasurementEmissionsCalculationDTO.builder()
            .globalWarmingPotential(BigDecimal.ONE)
            .annualGasFlow(BigDecimal.valueOf(100).setScale(5, RoundingMode.HALF_UP))
            .annualFossilAmountOfGreenhouseGas(new BigDecimal("1.5").setScale(5, RoundingMode.HALF_UP))
            .sustainableBiomassEmissions(new BigDecimal("0.5").setScale(5, RoundingMode.HALF_UP))
            .reportableEmissions(new BigDecimal("1.5").setScale(5, RoundingMode.HALF_UP))
            .build();

        MeasurementEmissionsCalculationDTO actual =
            measurementCO2EmissionsCalculationService.calculateEmissions(measurementCO2EmissionsCalculationParamsDTO);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void calculateEmissionsWithoutBiomass() {
        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .containsBiomass(false)
                .operationalHours(BigDecimal.TEN)
                .annualHourlyAverageFlueGasFlow(BigDecimal.TEN)
                .annualHourlyAverageGHGConcentration(BigDecimal.valueOf(20))
                .build();

        MeasurementEmissionsCalculationDTO expected = MeasurementEmissionsCalculationDTO.builder()
            .globalWarmingPotential(BigDecimal.ONE)
            .annualGasFlow(BigDecimal.valueOf(100).setScale(5, RoundingMode.HALF_UP))
            .annualFossilAmountOfGreenhouseGas(BigDecimal.valueOf(2).setScale(5, RoundingMode.HALF_UP))
            .sustainableBiomassEmissions(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP))
            .reportableEmissions(BigDecimal.valueOf(2).setScale(5, RoundingMode.HALF_UP))
            .build();

        MeasurementEmissionsCalculationDTO actual =
            measurementCO2EmissionsCalculationService.calculateEmissions(measurementCO2EmissionsCalculationParamsDTO);

        assertThat(actual).isEqualTo(expected);
    }
}

package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.n2o;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.GlobalWarmingPotential;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.MeasurementEmissionsCalculationService;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionsCalculationService;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MeasurementN2OEmissionsCalculationServiceTest {

    private MeasurementN2OEmissionsCalculationService measurementN2OEmissionsCalculationService =
        new MeasurementN2OEmissionsCalculationService(new MeasurementEmissionsCalculationService());

    @Test
    void calculateEmissionsWithBiomass() {
        MeasurementEmissionsCalculationParamsDTO measurementCO2EmissionsCalculationParamsDTO =
            MeasurementEmissionsCalculationParamsDTO.builder()
                .containsBiomass(true)
                .biomassPercentage(new BigDecimal("61.00001"))
                .operationalHours(new BigDecimal("102345678901"))
                .annualHourlyAverageFlueGasFlow(new BigDecimal("102345678901.1023456789012"))
                .annualHourlyAverageGHGConcentration(new BigDecimal("0.1023456789012"))
                .build();

        MeasurementEmissionsCalculationDTO expected = MeasurementEmissionsCalculationDTO.builder()
            .globalWarmingPotential(GlobalWarmingPotential.N2O.getValue())
            .annualGasFlow(new BigDecimal("10474637989717071205790.72707"))
            .annualFossilAmountOfGreenhouseGas(new BigDecimal("418093127954345200.26550"))
            .sustainableBiomassEmissions(new BigDecimal("194874360887569672565.93979"))
            .reportableEmissions(new BigDecimal("124591752130394869679.11852"))
            .build();

        MeasurementEmissionsCalculationDTO actual =
            measurementN2OEmissionsCalculationService.calculateEmissions(measurementCO2EmissionsCalculationParamsDTO);

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
            .globalWarmingPotential(GlobalWarmingPotential.N2O.getValue())
            .annualGasFlow(BigDecimal.valueOf(100).setScale(5, RoundingMode.HALF_UP))
            .annualFossilAmountOfGreenhouseGas(BigDecimal.valueOf(2).setScale(5, RoundingMode.HALF_UP))
            .sustainableBiomassEmissions(BigDecimal.ZERO.setScale(5, RoundingMode.HALF_UP))
            .reportableEmissions(BigDecimal.valueOf(596).setScale(5, RoundingMode.HALF_UP))
            .build();

        MeasurementEmissionsCalculationDTO actual =
            measurementN2OEmissionsCalculationService.calculateEmissions(measurementCO2EmissionsCalculationParamsDTO);

        assertThat(actual).isEqualTo(expected);
    }
}

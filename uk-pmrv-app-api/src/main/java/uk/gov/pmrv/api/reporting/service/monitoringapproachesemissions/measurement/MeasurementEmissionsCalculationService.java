package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class MeasurementEmissionsCalculationService {

    public MeasurementEmissionsCalculationDTO calculateEmissions(BigDecimal globalWarmingPotential,
                                                                 MeasurementEmissionsCalculationParamsDTO measurementEmissionsCalculationParamsDTO) {
        BigDecimal annualGasFlow = measurementEmissionsCalculationParamsDTO.getOperationalHours()
            .multiply(measurementEmissionsCalculationParamsDTO.getAnnualHourlyAverageFlueGasFlow());
        BigDecimal total =
            annualGasFlow.multiply(measurementEmissionsCalculationParamsDTO.getAnnualHourlyAverageGHGConcentration())
                .multiply(new BigDecimal("0.001"));
        BigDecimal biomass = BigDecimal.ZERO;
        if (measurementEmissionsCalculationParamsDTO.isContainsBiomass()) {
            biomass = measurementEmissionsCalculationParamsDTO.getBiomassPercentage()
                .divide(BigDecimal.valueOf(100),
                    measurementEmissionsCalculationParamsDTO.getBiomassPercentage().scale() + 2,
                    RoundingMode.HALF_UP);
        }
        BigDecimal annualFossilAmountOfGreenhouseGas = total.multiply(BigDecimal.ONE.subtract(biomass));
        BigDecimal reportableEmissions =
            annualFossilAmountOfGreenhouseGas.multiply(globalWarmingPotential).setScale(5,
                RoundingMode.HALF_UP);
        BigDecimal sustainableBiomassEmissions = total.multiply(globalWarmingPotential).multiply(biomass).setScale(5,
            RoundingMode.HALF_UP);

        return MeasurementEmissionsCalculationDTO.builder()
            .reportableEmissions(reportableEmissions)
            .sustainableBiomassEmissions(sustainableBiomassEmissions)
            .annualGasFlow(annualGasFlow.setScale(5, RoundingMode.HALF_UP))
            .globalWarmingPotential(globalWarmingPotential)
            .annualFossilAmountOfGreenhouseGas(annualFossilAmountOfGreenhouseGas.setScale(5, RoundingMode.HALF_UP))
            .build();
    }
}

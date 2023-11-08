package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.MeasurementEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeasurementN2OEmissionsValidatorTest {

    @Mock
    private MeasurementN2OEmissionsCalculationService measurementCO2EmissionsCalculationService;

    @InjectMocks
    private MeasurementN2OEmissionsValidator validator;

    @Test
    void validate() {
        MeasurementEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .reportableEmissions(BigDecimal.ONE)
            .sustainableBiomassEmissions(BigDecimal.ONE)
            .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
            .globalWarmingPotential(BigDecimal.ONE)
            .annualGasFlow(BigDecimal.ONE)
            .operationalHours(BigDecimal.ONE)
            .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
            .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
            .biomassPercentages(BiomassPercentages.builder()
                .contains(true)
                .biomassPercentage(BigDecimal.ONE)
                .nonSustainableBiomassPercentage(BigDecimal.ONE)
                .build())
            .build();

        MeasurementEmissionsCalculationDTO measurementEmissionsCalculationDTO =
            MeasurementEmissionsCalculationDTO.builder()
                .annualGasFlow(BigDecimal.ONE)
                .reportableEmissions(BigDecimal.ONE)
                .sustainableBiomassEmissions(BigDecimal.ONE)
                .globalWarmingPotential(BigDecimal.ONE)
                .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
                .build();

        when(measurementCO2EmissionsCalculationService.calculateEmissions(any(MeasurementEmissionsCalculationParamsDTO.class)))
            .thenReturn(measurementEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(0);

    }

    @Test
    void validate_violationWhenNotEqual() {
        MeasurementEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .reportableEmissions(BigDecimal.ONE)
            .sustainableBiomassEmissions(BigDecimal.ONE)
            .annualHourlyAverageGHGConcentration(BigDecimal.ONE)
            .globalWarmingPotential(BigDecimal.ONE)
            .annualGasFlow(BigDecimal.TEN)
            .operationalHours(BigDecimal.ONE)
            .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
            .annualHourlyAverageFlueGasFlow(BigDecimal.ONE)
            .biomassPercentages(BiomassPercentages.builder()
                .contains(true)
                .biomassPercentage(BigDecimal.ONE)
                .nonSustainableBiomassPercentage(BigDecimal.ONE)
                .build())
            .build();

        MeasurementEmissionsCalculationDTO measurementEmissionsCalculationDTO =
            MeasurementEmissionsCalculationDTO.builder()
                .annualGasFlow(BigDecimal.ONE)
                .reportableEmissions(BigDecimal.ONE)
                .sustainableBiomassEmissions(BigDecimal.ONE)
                .globalWarmingPotential(BigDecimal.ONE)
                .annualFossilAmountOfGreenhouseGas(BigDecimal.ONE)
                .build();

        when(measurementCO2EmissionsCalculationService.calculateEmissions(any(MeasurementEmissionsCalculationParamsDTO.class)))
            .thenReturn(measurementEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(1);

    }

}

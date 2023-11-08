package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AerManuallyProvidedEmissionsValidatorTest {
    private final AerManuallyProvidedEmissionsValidator aerManuallyProvidedEmissionsValidator =
        new AerManuallyProvidedEmissionsValidator();

    @Test
    public void validate_valid_whenCalculationCorrect() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
            MeasurementN2OEmissionPointEmission.builder()
                .calculationCorrect(Boolean.TRUE)
                .build();

        List<AerViolation> aerViolations =
            aerManuallyProvidedEmissionsValidator.validate(measurementEmissionPointEmission,
                AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(0);

    }

    @Test
    public void validate_valid_whenCalculationNotCorrectAndTotalEmissionsExist() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
            MeasurementN2OEmissionPointEmission.builder()
                .calculationCorrect(Boolean.FALSE)
                .biomassPercentages(BiomassPercentages.builder().contains(Boolean.TRUE).build())
                .providedEmissions(ManuallyProvidedEmissions.builder()
                    .totalProvidedSustainableBiomassEmissions(BigDecimal.TEN)
                    .build())
                .build();

        List<AerViolation> aerViolations =
            aerManuallyProvidedEmissionsValidator.validate(measurementEmissionPointEmission,
                AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(0);

    }

    @Test
    public void validate_invalid_whenCalculationNotCorrectAndTotalEmissionsNull() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
            MeasurementN2OEmissionPointEmission.builder()
                .calculationCorrect(Boolean.FALSE)
                .biomassPercentages(BiomassPercentages.builder().contains(Boolean.TRUE).build())
                .providedEmissions(ManuallyProvidedEmissions.builder()
                    .build())
                .build();

        List<AerViolation> aerViolations =
            aerManuallyProvidedEmissionsValidator.validate(measurementEmissionPointEmission,
                AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(1);
        assertThat(aerViolations.get(0).getMessage())
            .isEqualTo(AerViolation.AerViolationMessage.TOTAL_SUSTAINABLE_BIOMASS_EMISSION_ARE_MISSING.getMessage());

    }
}

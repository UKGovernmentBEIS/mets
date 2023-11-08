package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.FallbackEmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackBiomass;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.fallback.FallbackApproachEmissionsCalculationService;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerFallbackReportableEmissionsValidatorTest {

    @Mock
    private FallbackApproachEmissionsCalculationService fallbackApproachEmissionsCalculationService;

    @InjectMocks
    private AerFallbackReportableEmissionsValidator validator;

    @Test
    void validate_valid() {
        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
            .reportableEmissions(BigDecimal.TEN)
            .totalFossilEmissions(BigDecimal.TEN)
            .biomass(FallbackBiomass.builder()
                .totalNonSustainableBiomassEmissions(BigDecimal.TEN)
                .totalSustainableBiomassEmissions(BigDecimal.TEN)
                .totalEnergyContentFromBiomass(BigDecimal.TEN)
                .contains(true)
                .build())
            .build();

        FallbackEmissionsCalculationDTO fallbackEmissionsCalculationDTO = FallbackEmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.TEN)
            .build();

        when(fallbackApproachEmissionsCalculationService.calculateEmissions(any(FallbackEmissionsCalculationParamsDTO.class)))
            .thenReturn(fallbackEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(fallbackEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(0);
    }

    @Test
    void validate_invalid() {
        FallbackEmissions fallbackEmission = FallbackEmissions.builder()
            .reportableEmissions(BigDecimal.TEN)
            .totalFossilEmissions(BigDecimal.TEN)
            .biomass(FallbackBiomass.builder()
                .totalNonSustainableBiomassEmissions(BigDecimal.TEN)
                .totalSustainableBiomassEmissions(BigDecimal.TEN)
                .totalEnergyContentFromBiomass(BigDecimal.TEN)
                .contains(true)
                .build())
            .build();

        FallbackEmissionsCalculationDTO fallbackEmissionsCalculationDTO = FallbackEmissionsCalculationDTO.builder()
            .reportableEmissions(BigDecimal.ONE)
            .build();

        when(fallbackApproachEmissionsCalculationService.calculateEmissions(any(FallbackEmissionsCalculationParamsDTO.class)))
            .thenReturn(fallbackEmissionsCalculationDTO);

        List<AerViolation> aerViolations = validator.validate(fallbackEmission,
            AerContainer.builder().build());

        assertThat(aerViolations.size()).isEqualTo(1);
        assertThat(aerViolations.get(0).getMessage())
            .isEqualTo(AerViolation.AerViolationMessage.MEASUREMENT_INCORRECT_TOTAL_EMISSIONS.getMessage());
    }
}
package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.fallback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback.FallbackEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerFallbackEmissionsValidatorTest {

    private AerFallbackEmissionsValidator validator;

    @Mock
    private AerFallbackReferencesValidator fallbackReferencesValidator;

    @BeforeEach
    void setUp() {
        validator = new AerFallbackEmissionsValidator(List.of(fallbackReferencesValidator));
    }

    @Test
    void validate() {
        FallbackEmissions fallbackEmissions = FallbackEmissions.builder()
            .build();

        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of(MonitoringApproachType.FALLBACK, fallbackEmissions)).build()
                ).build())
            .build();

        when(fallbackReferencesValidator.validate(fallbackEmissions, aerContainer))
            .thenReturn(List.of());

        AerValidationResult result = validator.validate(aerContainer);
        assertThat(result.getAerViolations()).isEmpty();
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_invalid() {
        FallbackEmissions fallbackEmissions = FallbackEmissions.builder()

            .build();

        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of(MonitoringApproachType.FALLBACK, fallbackEmissions)).build()
                ).build())
            .build();

        when(fallbackReferencesValidator.validate(fallbackEmissions, aerContainer))
            .thenReturn(List.of(new AerViolation(FallbackEmissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM)));

        AerValidationResult result = validator.validate(aerContainer);
        assertThat(result.getAerViolations()).isNotEmpty();
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void validate_noFallbackEmissions() {
        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_PFC,
                        CalculationOfPfcEmissions.builder().build())).build()
                ).build())
            .build();

        AerValidationResult result = validator.validate(aerContainer);
        assertThat(result.getAerViolations()).isEmpty();
        assertThat(result.isValid()).isTrue();
    }
}
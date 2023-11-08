package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.n2o;

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
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.AerMeasurementReferencesValidator;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerMeasurementN2OEmissionsValidatorTest {

    private AerMeasurementN2OEmissionsValidator validator;

    @Mock
    private AerMeasurementReferencesValidator measurementReferencesValidator;

    @Mock
    private MeasurementN2OEmissionsValidator measurementN2OEmissionsValidator;

    @BeforeEach
    void setUp() {
        validator = new AerMeasurementN2OEmissionsValidator(List.of(measurementReferencesValidator),
            List.of(measurementN2OEmissionsValidator));
    }

    @Test
    void validate_valid() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
            MeasurementN2OEmissionPointEmission.builder()
                .emissionPoint("1")
                .build();
        MeasurementOfN2OEmissions measurementEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementEmissions))
            .build();
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        when(measurementReferencesValidator.
            validate(measurementEmissionPointEmission, aerContainer))
            .thenReturn(List.of());

        when(measurementN2OEmissionsValidator
            .validate(measurementEmissionPointEmission, aerContainer))
            .thenReturn(List.of());

        AerValidationResult aerValidationResult = validator
            .validate(aerContainer);
        assertNotNull(aerValidationResult);
        assertTrue(aerValidationResult.isValid());
        assertThat(aerValidationResult.getAerViolations()).isEmpty();
    }

    @Test
    void validate_invalid() {
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission =
            MeasurementN2OEmissionPointEmission.builder()
                .emissionPoint("1")
                .build();
        MeasurementOfN2OEmissions measurementEmissions = MeasurementOfN2OEmissions.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointEmissions(List.of(measurementEmissionPointEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_N2O, measurementEmissions))
            .build();
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = List.of(
            new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(),
                AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM)
        );

        when(measurementReferencesValidator.
            validate(measurementEmissionPointEmission, aerContainer))
            .thenReturn(aerViolations);

        when(measurementN2OEmissionsValidator
            .validate(measurementEmissionPointEmission, aerContainer))
            .thenReturn(List.of());

        AerValidationResult aerValidationResult = validator
            .validate(aerContainer);
        assertNotNull(aerValidationResult);
        assertFalse(aerValidationResult.isValid());
        assertThat(aerValidationResult.getAerViolations()).isNotEmpty();
        assertEquals(aerViolations, aerValidationResult.getAerViolations());
    }

    @Test
    void validate_no_calculation_emissions() {
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(MonitoringApproachEmissions.builder().build())
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        AerValidationResult aerValidationResult = validator
            .validate(aerContainer);
        assertNotNull(aerValidationResult);
        assertTrue(aerValidationResult.isValid());
        assertThat(aerValidationResult.getAerViolations()).isEmpty();

        verifyNoInteractions(measurementReferencesValidator);
        verifyNoInteractions(measurementN2OEmissionsValidator);
    }
}

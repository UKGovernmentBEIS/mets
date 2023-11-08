package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

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
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2.AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator;
import uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement.co2.AerMeasurementCO2EmissionsValidator;

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
class AerMeasurementCO2EmissionsValidatorTest {

    private AerMeasurementCO2EmissionsValidator validator;

    @Mock
    private AerMeasurementReferencesValidator measurementReferencesValidator;

    @Mock
    private AerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator aerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator;

    @BeforeEach
    void setUp() {
        validator = new AerMeasurementCO2EmissionsValidator(
            List.of(measurementReferencesValidator),
            List.of(aerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator)
        );
    }

    @Test
    void validate_valid() {
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission = MeasurementCO2EmissionPointEmission.builder()
                .emissionPoint("1")
                .build();
        MeasurementOfCO2Emissions measurementEmissions = MeasurementOfCO2Emissions.builder()
                .type(MonitoringApproachType.MEASUREMENT_CO2)
                .emissionPointEmissions(List.of(measurementEmissionPointEmission))
                .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementEmissions))
                .build();
        Aer aer = Aer.builder()
                .monitoringApproachEmissions(monitoringApproachEmissions)
                .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        when(measurementReferencesValidator.
                validate(measurementEmissionPointEmission, aerContainer))
                .thenReturn(List.of());

        when(aerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator
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
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission = MeasurementCO2EmissionPointEmission.builder()
                .emissionPoint("1")
                .build();
        MeasurementOfCO2Emissions measurementEmissions = MeasurementOfCO2Emissions.builder()
                .type(MonitoringApproachType.MEASUREMENT_CO2)
                .emissionPointEmissions(List.of(measurementEmissionPointEmission))
                .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
                .monitoringApproachEmissions(Map.of(MonitoringApproachType.MEASUREMENT_CO2, measurementEmissions))
                .build();
        Aer aer = Aer.builder()
                .monitoringApproachEmissions(monitoringApproachEmissions)
                .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = List.of(
                new AerViolation(MeasurementOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM)
        );

        when(measurementReferencesValidator.
                validate(measurementEmissionPointEmission, aerContainer))
                .thenReturn(aerViolations);

        when(aerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator
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
        verifyNoInteractions(aerMeasurementCO2EmissionPointParamMonitoringTierDiffReasonValidator);
    }
}

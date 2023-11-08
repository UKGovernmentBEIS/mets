package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerValidationResult;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;

import java.util.ArrayList;
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
class AerCalculationEmissionsValidatorTest {

    @InjectMocks
    private AerCalculationEmissionsValidator calculationEmissionsValidator;

    @Spy
    private ArrayList<AerCalculationSourceStreamEmissionValidator> calculationSourceStreamEmissionValidators;

    @Mock
    private AerCalculationSourceStreamBiomassMonitoringTierValidator aerCalculationSourceStreamBiomassMonitoringTierValidator;

    @BeforeEach
    void setUp() {
        calculationSourceStreamEmissionValidators.add(aerCalculationSourceStreamBiomassMonitoringTierValidator);
    }

    @Test
    void validate_valid() {
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        when(aerCalculationSourceStreamBiomassMonitoringTierValidator.
            validate(calculationSourceStreamEmission, aerContainer))
            .thenReturn(List.of());

        AerValidationResult aerValidationResult = calculationEmissionsValidator
                .validate(aerContainer);
        assertNotNull(aerValidationResult);
        assertTrue(aerValidationResult.isValid());
        assertThat(aerValidationResult.getAerViolations()).isEmpty();
    }

    @Test
    void validate_invalid() {
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream("1")
            .build();
        CalculationOfCO2Emissions calculationEmissions = CalculationOfCO2Emissions.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamEmissions(List.of(calculationSourceStreamEmission))
            .build();
        MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(Map.of(MonitoringApproachType.CALCULATION_CO2, calculationEmissions))
            .build();
        Aer aer = Aer.builder()
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = List.of(
            new AerViolation(CalculationOfCO2Emissions.class.getSimpleName(), AerViolation.AerViolationMessage.CALCULATION_INVALID_PARAMETER_MONITORING_TIER)
        );

        when(aerCalculationSourceStreamBiomassMonitoringTierValidator.
            validate(calculationSourceStreamEmission, aerContainer))
            .thenReturn(aerViolations);

        AerValidationResult aerValidationResult = calculationEmissionsValidator
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

        AerValidationResult aerValidationResult = calculationEmissionsValidator
                .validate(aerContainer);
        assertNotNull(aerValidationResult);
        assertTrue(aerValidationResult.isValid());
        assertThat(aerValidationResult.getAerViolations()).isEmpty();

        verifyNoInteractions(aerCalculationSourceStreamBiomassMonitoringTierValidator);
    }
}
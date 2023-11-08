package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.pfc;

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
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationOfPfcEmissionsValidatorTest {

    private AerCalculationOfPfcEmissionsValidator validator;

    @Mock
    private AerCalculationOfPfcSourceStreamEmissionValidator aerCalculationOfPfcSourceStreamEmissionValidator;

    @BeforeEach
    void setup() {
        validator =
            new AerCalculationOfPfcEmissionsValidator(List.of(aerCalculationOfPfcSourceStreamEmissionValidator));
    }

    @Test
    void valid_when_monitoring_approaches_not_contain_pfc() {
        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of())
                    .build())
                .build())
            .build();

        AerValidationResult validationResult = validator.validate(aerContainer);

        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void valid_when_monitoring_approaches_contain_pfc_and_source_stream_emissions_are_valid() {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder().build();
        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of(
                        MonitoringApproachType.CALCULATION_PFC, CalculationOfPfcEmissions.builder()
                            .sourceStreamEmissions(List.of(
                                pfcSourceStreamEmission
                            ))
                            .build()
                    ))
                    .build())
                .build())
            .build();

        when(aerCalculationOfPfcSourceStreamEmissionValidator.validate(pfcSourceStreamEmission, aerContainer))
            .thenReturn(Collections.emptyList());

        AerValidationResult validationResult = validator.validate(aerContainer);

        assertThat(validationResult.isValid()).isTrue();
    }

    @Test
    void invalid_when_monitoring_approaches_contain_pfc_and_source_stream_emissions_are_invalid() {
        PfcSourceStreamEmission pfcSourceStreamEmission = PfcSourceStreamEmission.builder().build();
        AerContainer aerContainer = AerContainer.builder()
            .aer(Aer.builder()
                .monitoringApproachEmissions(MonitoringApproachEmissions.builder()
                    .monitoringApproachEmissions(Map.of(
                        MonitoringApproachType.CALCULATION_PFC, CalculationOfPfcEmissions.builder()
                            .sourceStreamEmissions(List.of(
                                pfcSourceStreamEmission
                            ))
                            .build()
                    ))
                    .build())
                .build())
            .build();

        when(aerCalculationOfPfcSourceStreamEmissionValidator.validate(pfcSourceStreamEmission, aerContainer))
            .thenReturn(List.of(new AerViolation(AerCalculationOfPfcEmissionsValidator.class.getSimpleName(),
                AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE)));

        AerValidationResult validationResult = validator.validate(aerContainer);

        assertThat(validationResult.isValid()).isFalse();
    }
}
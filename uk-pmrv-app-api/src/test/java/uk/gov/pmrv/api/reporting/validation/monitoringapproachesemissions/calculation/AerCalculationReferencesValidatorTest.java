package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.calculation;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.validation.AerReferenceService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AerCalculationReferencesValidatorTest {

    @InjectMocks
    private AerCalculationReferencesValidator validator;

    @Mock
    private AerReferenceService aerReferenceService;

    @Test
    void validate_valid() {
        String sourceStreamId = "SS1";
        String emissionSourceId = "ES1";
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .emissionSources(Set.of(emissionSourceId))
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
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(SourceStream.builder().id(sourceStreamId).build()))
                .build())
            .emissionSources(EmissionSources.builder()
                .emissionSources(List.of(EmissionSource.builder().id(emissionSourceId).build()))
                .build())
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isEmpty();

        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getSourceStreamsIds(), List.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST);
    }

    @Test
    void validate_invalid() {
        String sourceStreamId = "SS1";
        String emissionSourceId = "ES1";
        CalculationSourceStreamEmission calculationSourceStreamEmission = CalculationSourceStreamEmission.builder()
            .sourceStream(sourceStreamId)
            .emissionSources(Set.of(emissionSourceId))
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
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(SourceStream.builder().id("SS2").build()))
                .build())
            .emissionSources(EmissionSources.builder()
                .emissionSources(List.of(EmissionSource.builder().id("ES2").build()))
                .build())
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        when(aerReferenceService
            .validateExistenceInAer(aer.getSourceStreamsIds(), List.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM, List.of(sourceStreamId))));
        when(aerReferenceService
            .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST))
            .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE, List.of(emissionSourceId))));

        List<AerViolation> aerViolations = validator.validate(calculationSourceStreamEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(2, aerViolations.size());

        assertThat(aerViolations).extracting(AerViolation::getSectionName).containsOnly("CalculationOfCO2Emissions");
        assertThat(aerViolations).extracting(AerViolation::getMessage).containsExactlyInAnyOrder(
            AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM.getMessage(),
            AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE.getMessage()
        );

        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getSourceStreamsIds(), List.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST);
    }
}
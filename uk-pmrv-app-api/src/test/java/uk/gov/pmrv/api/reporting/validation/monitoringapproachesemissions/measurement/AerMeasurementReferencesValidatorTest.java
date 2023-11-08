package uk.gov.pmrv.api.reporting.validation.monitoringapproachesemissions.measurement;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerViolation;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.MeasurementEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
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
class AerMeasurementReferencesValidatorTest {

    @InjectMocks
    private AerMeasurementReferencesValidator validator;

    @Mock
    private AerReferenceService aerReferenceService;

    @Test
    void validate_valid_CO2() {
        String sourceStreamId = "SS1";
        String emissionSourceId = "ES1";
        String emissionPointId = "EP1";
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission = MeasurementCO2EmissionPointEmission.builder()
                .sourceStreams(Set.of(sourceStreamId))
                .emissionSources(Set.of(emissionSourceId))
                .emissionPoint(emissionPointId)
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
                .sourceStreams(SourceStreams.builder()
                        .sourceStreams(List.of(SourceStream.builder().id(sourceStreamId).build()))
                        .build())
                .emissionSources(EmissionSources.builder()
                        .emissionSources(List.of(EmissionSource.builder().id(emissionSourceId).build()))
                        .build())
                .emissionPoints(EmissionPoints.builder()
                        .emissionPoints(List.of(EmissionPoint.builder().id(emissionPointId).build()))
                        .build())
                .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isEmpty();

        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getSourceStreamsIds(), Set.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getEmissionPointsIds(), List.of(emissionPointId), AerReferenceService.Rule.EMISSION_POINTS_EXIST);

    }

    @Test
    void validate_valid_N2O() {
        String sourceStreamId = "SS1";
        String emissionSourceId = "ES1";
        String emissionPointId = "EP1";
        MeasurementN2OEmissionPointEmission measurementEmissionPointEmission = MeasurementN2OEmissionPointEmission.builder()
            .sourceStreams(Set.of(sourceStreamId))
            .emissionSources(Set.of(emissionSourceId))
            .emissionPoint(emissionPointId)
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
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(SourceStream.builder().id(sourceStreamId).build()))
                .build())
            .emissionSources(EmissionSources.builder()
                .emissionSources(List.of(EmissionSource.builder().id(emissionSourceId).build()))
                .build())
            .emissionPoints(EmissionPoints.builder()
                .emissionPoints(List.of(EmissionPoint.builder().id(emissionPointId).build()))
                .build())
            .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isEmpty();

        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getSourceStreamsIds(), Set.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(aerReferenceService, times(1))
            .validateExistenceInAer(aer.getEmissionPointsIds(), List.of(emissionPointId), AerReferenceService.Rule.EMISSION_POINTS_EXIST);

    }

    @Test
    void validate_invalid() {
        String sourceStreamId = "SS1";
        String emissionSourceId = "ES1";
        String emissionPointId = "EP1";
        MeasurementCO2EmissionPointEmission measurementEmissionPointEmission = MeasurementCO2EmissionPointEmission.builder()
                .sourceStreams(Set.of(sourceStreamId))
                .emissionSources(Set.of(emissionSourceId))
                .emissionPoint(emissionPointId)
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
                .sourceStreams(SourceStreams.builder()
                        .sourceStreams(List.of(SourceStream.builder().id("SS2").build()))
                        .build())
                .emissionSources(EmissionSources.builder()
                        .emissionSources(List.of(EmissionSource.builder().id("ES2").build()))
                        .build())
                .emissionPoints(EmissionPoints.builder()
                        .emissionPoints(List.of(EmissionPoint.builder().id("EP2").build()))
                        .build())
                .build();
        AerContainer aerContainer = AerContainer.builder().aer(aer).build();

        when(aerReferenceService
                .validateExistenceInAer(aer.getSourceStreamsIds(), Set.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS))
                .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM, List.of(sourceStreamId))));
        when(aerReferenceService
                .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST))
                .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE, List.of(emissionSourceId))));
        when(aerReferenceService
                .validateExistenceInAer(aer.getEmissionPointsIds(), List.of(emissionPointId), AerReferenceService.Rule.EMISSION_POINTS_EXIST))
                .thenReturn(Optional.of(Pair.of(AerViolation.AerViolationMessage.INVALID_EMISSION_POINT, List.of(emissionPointId))));

        List<AerViolation> aerViolations = validator.validate(measurementEmissionPointEmission, aerContainer);
        assertThat(aerViolations).isNotEmpty();
        assertEquals(3, aerViolations.size());

        assertThat(aerViolations).extracting(AerViolation::getSectionName).containsOnly(MeasurementEmissionPointEmission.class.getSimpleName());
        assertThat(aerViolations).extracting(AerViolation::getMessage).containsExactlyInAnyOrder(
                AerViolation.AerViolationMessage.INVALID_SOURCE_STREAM.getMessage(),
                AerViolation.AerViolationMessage.INVALID_EMISSION_SOURCE.getMessage(),
                AerViolation.AerViolationMessage.INVALID_EMISSION_POINT.getMessage()
        );

        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getSourceStreamsIds(), Set.of(sourceStreamId), AerReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getEmissionSourcesIds(), Set.of(emissionSourceId), AerReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(aerReferenceService, times(1))
                .validateExistenceInAer(aer.getEmissionPointsIds(), List.of(emissionPointId), AerReferenceService.Rule.EMISSION_POINTS_EXIST);
    }
}

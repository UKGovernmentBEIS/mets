package uk.gov.pmrv.api.permit.validation;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.InstallationDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2O;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.TransferN2ODirection;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class N2OMonitoringApproachSectionValidatorTest {

    @InjectMocks
    private N2OMonitoringApproachSectionValidator validator;

    @Mock
    private PermitReferenceService permitReferenceService;

    private static String sourceStreamId1, sourceStreamId2,
        emissionSourceId1, emissionSourceId2,
        emissionPointId1, emissionPointId2,
        measurementDeviceOrMethodId1, measurementDeviceOrMethodId2;
    private static SourceStream sourceStream1, sourceStream2;
    private static EmissionSource emissionSource1, emissionSource2;
    private static EmissionPoint emissionPoint1, emissionPoint2;
    private static MeasurementDeviceOrMethod measurementDeviceOrMethod1, measurementDeviceOrMethod2;
    private static UUID file1, file2;

    @BeforeAll
    static void setup() {
        sourceStreamId1 = UUID.randomUUID().toString();
        sourceStreamId2 = UUID.randomUUID().toString();
        emissionSourceId1 = UUID.randomUUID().toString();
        emissionSourceId2 = UUID.randomUUID().toString();
        emissionPointId1 = UUID.randomUUID().toString();
        emissionPointId2 = UUID.randomUUID().toString();
        measurementDeviceOrMethodId1 = UUID.randomUUID().toString();
        measurementDeviceOrMethodId2 = UUID.randomUUID().toString();
        file1 = UUID.randomUUID();
        file2 = UUID.randomUUID();

        sourceStream1 = SourceStream.builder().id(sourceStreamId1).reference("sourceStream1").build();
        sourceStream2 = SourceStream.builder().id(sourceStreamId2).reference("sourceStream2").build();
        emissionSource1 = EmissionSource.builder().id(emissionSourceId1).reference("emissionSource1").build();
        emissionSource2 = EmissionSource.builder().id(emissionSourceId2).reference("emissionSource2").build();
        emissionPoint1 = EmissionPoint.builder().id(emissionPointId1).reference("emissionPoint1").build();
        emissionPoint2 = EmissionPoint.builder().id(emissionPointId2).reference("emissionPoint2").build();
        measurementDeviceOrMethod1 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId1).build();
        measurementDeviceOrMethod2 = MeasurementDeviceOrMethod.builder().id(measurementDeviceOrMethodId2).build();
    }

    @Test
    void validatePermitContainer() {
        final TransferN2O transfer = buildTransferredN2O();
        final MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(
            Set.of(sourceStreamId1, sourceStreamId2),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            MeasurementOfN2OMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build(), transfer);
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(MonitoringApproachType.MEASUREMENT_N2O, monitoringApproach))
                .build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_no_N2O() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2MonitoringApproach.builder().build()))
                .build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validateSection() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            MeasurementOfN2OMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build(), null);

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_source_stream() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId2),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            MeasurementOfN2OMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build(), null);

        when(permitReferenceService.validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                List.of(sourceStream2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasurementOfN2OMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_SOURCE_STREAM,
                List.of(sourceStream2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_source() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            MeasurementOfN2OMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build(), null);

        when(permitReferenceService.validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
            .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                List.of(emissionSource2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasurementOfN2OMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_EMISSION_SOURCE,
                List.of(emissionSource2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_invalid_emission_point() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            MeasurementOfN2OMeasuredEmissions.builder()
                .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1))
                .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
                .highestRequiredTier(HighestRequiredTier.builder().build())
                .build(), null);

        when(permitReferenceService.validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
            .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                List.of(emissionPoint2.getId()))));

        PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
            new PermitViolation(MeasurementOfN2OMonitoringApproach.class.getSimpleName(),
                PermitViolation.PermitViolationMessage.INVALID_EMISSION_POINT,
                List.of(emissionPoint2.getId()).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permit.getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateSection_null_section() {
        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();
        PermitContainer permitContainer = PermitContainer
            .builder().permit(permit)
            .build();

        PermitValidationResult result = validator.validate(null, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validateMeasuredEmissionsMeasurementDevicesOrMethodsExist_whenSomeNotExist_thenPermitViolation() {
        final PermitContainer permitContainer = buildPermitContainer();

        final MeasurementOfN2OMeasuredEmissions n2OMeasuredEmissions = MeasurementOfN2OMeasuredEmissions.builder()
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .measurementDevicesOrMethods(Set.of("1", measurementDeviceOrMethodId1))
            .build();

        final MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            n2OMeasuredEmissions, null);

        when(permitReferenceService.validateExistenceInPermit(
            permitContainer.getPermit().getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permitContainer.getPermit().getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permitContainer.getPermit().getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST))
            .thenReturn(Optional.empty());

        when(permitReferenceService.validateExistenceInPermit(
            permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST))
            .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                List.of("1"))));

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(new PermitViolation(MeasurementOfN2OMonitoringApproach.class.getSimpleName(),
            PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
            List.of("1").toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateMeasuredEmissionsMeasurementDevicesOrMethodsExist_whenAllExist_thenAllow() {
        final PermitContainer permitContainer = buildPermitContainer();

        final MeasurementOfN2OMeasuredEmissions n2OMeasuredEmissions = MeasurementOfN2OMeasuredEmissions.builder()
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_3)
            .highestRequiredTier(HighestRequiredTier.builder().build())
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .measurementDevicesOrMethods(Set.of(measurementDeviceOrMethodId1, measurementDeviceOrMethodId2))
            .build();

        final MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            n2OMeasuredEmissions, null);

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validateMeasuredEmissionsFilesExist_whenAllExist_thenAllow() {
        final PermitContainer permitContainer = buildPermitContainer();

        final MeasurementOfN2OMeasuredEmissions n2OMeasuredEmissions = MeasurementOfN2OMeasuredEmissions.builder()
            .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_1)
            .samplingFrequency(MeasuredEmissionsSamplingFrequency.DAILY)
            .highestRequiredTier(HighestRequiredTier.builder()
                .isHighestRequiredTier(Boolean.FALSE)
                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                    .isCostUnreasonable(Boolean.TRUE)
                    .files(Set.of(file1, file2))
                    .build())
                .build())
            .build();

        final MeasurementOfN2OMonitoringApproach monitoringApproach = buildN2OMonitoringApproach(Set.of(sourceStreamId1),
            Set.of(emissionSourceId1, emissionSourceId2),
            emissionPointId1,
            n2OMeasuredEmissions, null);

        final PermitValidationResult result = validator.validate(monitoringApproach, permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getSourceStreamsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getSourceStreams(),
            PermitReferenceService.Rule.SOURCE_STREAM_EXISTS);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionSourcesIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionSources(),
            PermitReferenceService.Rule.EMISSION_SOURCES_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getEmissionPointsIds(),
            Collections.singletonList(monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getEmissionPointCategory().getEmissionPoint()),
            PermitReferenceService.Rule.EMISSION_POINTS_EXIST);
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
            permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
            monitoringApproach.getEmissionPointCategoryAppliedTiers().get(0).getMeasuredEmissions().getMeasurementDevicesOrMethods(),
            PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    private MeasurementOfN2OMonitoringApproach buildN2OMonitoringApproach(
        Set<String> sourceStreams,
        Set<String> emissionSources,
        String emissionPoint,
        MeasurementOfN2OMeasuredEmissions measuredEmissions,
        TransferN2O transfer) {
        return MeasurementOfN2OMonitoringApproach.builder()
            .hasTransfer(transfer != null)
            .emissionPointCategoryAppliedTiers(List.of(MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                    .sourceStreams(sourceStreams)
                    .transfer(transfer)
                    .emissionSources(emissionSources)
                    .emissionPoint(emissionPoint)
                    .emissionType(MeasurementOfN2OEmissionType.ABATED)
                    .monitoringApproachType(MeasurementOfN2OMonitoringApproachType.CALCULATION)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(26000.1))
                    .categoryType(CategoryType.MAJOR)
                    .build())
                .measuredEmissions(measuredEmissions)
                .build()))
            .build();
    }

    private PermitContainer buildPermitContainer() {

        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(sourceStream1, sourceStream2)).build())
            .emissionSources(
                EmissionSources.builder().emissionSources(List.of(emissionSource1, emissionSource2)).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(emissionPoint1, emissionPoint2)).build())
            .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder()
                .measurementDevicesOrMethods(List.of(measurementDeviceOrMethod1, measurementDeviceOrMethod2)).build())
            .build();

        return PermitContainer.builder()
            .permit(permit)
            .permitAttachments(Map.of(file1, "file1", file2, "file2"))
            .build();
    }

    private TransferN2O buildTransferredN2O() {
        return TransferN2O.builder()
            .entryAccountingForTransfer(true)
            .transferDirection(TransferN2ODirection.EXPORTED_TO_LONG_TERM_FACILITY)
            .installationDetails(InstallationDetails.builder()
                .installationName("Name")
                .line1("line1")
                .line2("line2")
                .email("test@test.com")
                .city("city")
                .postcode("postcode")
                .build())
            .build();
    }
}

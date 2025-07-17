package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.MonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementCO2EmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.co2.MeasurementOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementN2OEmissionPointEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.n2o.MeasurementOfN2OEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationOfPfcEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.CalculationPfcParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc.PfcSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerEmissionPointsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerEmissionSourcesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerMonitoringApproachEmissionsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerRegulatedActivitiesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSectionInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init.AerSourceStreamsInitializationService;

@ExtendWith(MockitoExtension.class)
class AerInitializationServiceTest {

    @Mock
    private AerEmissionPointsInitializationService aerEmissionPointsInitService;

    @Mock
    private AerMonitoringApproachEmissionsInitializationService aerMonitoringApproachEmissionsInitializationService;

    @Mock
    private AerEmissionSourcesInitializationService aerEmissionSourcesInitService;

    @Mock
    private AerSourceStreamsInitializationService aerSourceStreamsInitService;

    @Mock
    private AerRegulatedActivitiesInitializationService aerRegulatedActivitiesInitializationService;

    @Spy
    private ArrayList<AerSectionInitializationService> aerSectionInitializationServices;

    @InjectMocks
    private AerInitializationService aerInitializationService;

    @BeforeEach
    void setUp() {
        aerSectionInitializationServices.add(aerEmissionPointsInitService);
        aerSectionInitializationServices.add(aerMonitoringApproachEmissionsInitializationService);
        aerSectionInitializationServices.add(aerEmissionSourcesInitService);
        aerSectionInitializationServices.add(aerSourceStreamsInitService);
        aerSectionInitializationServices.add(aerRegulatedActivitiesInitializationService);
    }

    @Test
    void initializeAer() {

        final SourceStreams sourceStreams = SourceStreams.builder()
            .sourceStreams(List.of(
                SourceStream.builder()
                    .type(SourceStreamType.COMBUSTION_FLARES)
                    .reference("reference 1")
                    .build(),
                SourceStream.builder()
                    .type(SourceStreamType.CEMENT_CLINKER_CKD)
                    .reference("reference 2")
                    .build()))
            .build();

        final MonitoringApproaches monitoringApproaches = MonitoringApproaches.builder()
            .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION_CO2,
                CalculationOfCO2MonitoringApproach.builder()
                    .sourceStreamCategoryAppliedTiers(List.of(
                        CalculationSourceStreamCategoryAppliedTier.builder()
                            .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                                .sourceStream("SS0001")
                                .emissionSources(Set.of("ES0001"))
                                .categoryType(CategoryType.MAJOR)
                                .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                                .build())
                            .activityData(CalculationActivityData.builder().tier(CalculationActivityDataTier.TIER_3).build())
                            .netCalorificValue(
                                CalculationNetCalorificValue.builder().tier(CalculationNetCalorificValueTier.TIER_2B).build())
                            .build())
                    )
                    .build())
            )
            .build();

        final EmissionSources emissionSources = EmissionSources.builder()
            .emissionSources(List.of(
                EmissionSource.builder()
                    .reference("reference 1")
                    .description("description 1")
                    .build(),
                EmissionSource.builder()
                    .reference("reference 2")
                    .description("description 2")
                    .build()
            )).build();

        final EmissionPoints emissionPoints = EmissionPoints.builder()
            .emissionPoints(List.of(EmissionPoint.builder().reference("ep ref 1").description("ep desc 1").build()))
            .build();

        final MonitoringApproachEmissions monitoringApproachEmissions = MonitoringApproachEmissions.builder()
            .monitoringApproachEmissions(
                Map.of(
                    MonitoringApproachType.CALCULATION_CO2,
                    CalculationOfCO2Emissions.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .sourceStreamEmissions(List.of(CalculationSourceStreamEmission.builder()
                            .id("SS-UUID-1")
                            .sourceStream("SS0001")
                            .emissionSources(Set.of("ES0001"))
                            .parameterMonitoringTiers(List.of(
                                    CalculationActivityDataMonitoringTier.builder()
                                        .type(CalculationParameterType.ACTIVITY_DATA)
                                        .tier(CalculationActivityDataTier.TIER_3)
                                        .build(),
                                    CalculationNetCalorificValueMonitoringTier.builder()
                                        .type(CalculationParameterType.NET_CALORIFIC_VALUE)
                                        .tier(CalculationNetCalorificValueTier.TIER_2B)
                                        .build()
                                )
                            )
                            .build())
                        )
                        .build(),
                    MonitoringApproachType.MEASUREMENT_CO2,
                    MeasurementOfCO2Emissions.builder()
                        .type(MonitoringApproachType.MEASUREMENT_CO2)
                        .emissionPointEmissions(List.of(
                            MeasurementCO2EmissionPointEmission.builder()
                                .id("EP-UUID-1")
                                .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_2)
                                .build()
                        ))
                        .build(),
                    MonitoringApproachType.MEASUREMENT_N2O,
                    MeasurementOfN2OEmissions.builder()
                        .type(MonitoringApproachType.MEASUREMENT_N2O)
                        .emissionPointEmissions(List.of(
                            MeasurementN2OEmissionPointEmission.builder()
                                .id("EP-UUID-2")
                                .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_2)
                                .build()
                        ))
                        .build(),
                    MonitoringApproachType.CALCULATION_PFC,
                    CalculationOfPfcEmissions.builder()
                        .type(MonitoringApproachType.CALCULATION_PFC)
                        .sourceStreamEmissions(List.of(
                            PfcSourceStreamEmission.builder()
                                .id("SS-UUID-2")
                                .parameterMonitoringTier(CalculationPfcParameterMonitoringTier.builder().build())
                                .build()
                        ))
                        .build()
                )
            )
            .build();

        final RegulatedActivities regulatedActivities = RegulatedActivities.builder().regulatedActivities(
            List.of(RegulatedActivity.builder()
                .type(RegulatedActivityType.COMBUSTION)
                .capacity(new BigDecimal(11))
                .capacityUnit(CapacityUnit.KW)
                .build())
        ).build();

        final Permit permit = Permit.builder()
            .sourceStreams(sourceStreams)
            .monitoringApproaches(monitoringApproaches)
            .emissionSources(emissionSources)
            .emissionPoints(emissionPoints)
            .regulatedActivities(regulatedActivities)
            .build();

        Aer expected = Aer.builder()
            .sourceStreams(sourceStreams)
            .monitoringApproachEmissions(monitoringApproachEmissions)
            .emissionSources(emissionSources)
            .emissionPoints(emissionPoints)
            .regulatedActivities((AerRegulatedActivities.builder()
                .regulatedActivities(List.of(AerRegulatedActivity.builder()
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(new BigDecimal(11))
                    .capacityUnit(CapacityUnit.KW)
                    .build()))
                .build()))
            .build();

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionPoints(emissionPoints);
            return null;
        }).when(aerEmissionPointsInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setMonitoringApproachEmissions(monitoringApproachEmissions);
            return null;
        }).when(aerMonitoringApproachEmissionsInitializationService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionSources(emissionSources);
            return null;
        }).when(aerEmissionSourcesInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setSourceStreams(sourceStreams);
            return null;
        }).when(aerSourceStreamsInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setRegulatedActivities(AerRegulatedActivities.builder()
                .regulatedActivities(List.of(AerRegulatedActivity.builder()
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(new BigDecimal(11))
                    .capacityUnit(CapacityUnit.KW)
                    .build()))
                .build());
            return null;
        }).when(aerRegulatedActivitiesInitializationService).initialize(any(Aer.class), eq(permit));

        // Invoke
        Aer actual = aerInitializationService.initializeAer(permit);

        // Verify
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }
}
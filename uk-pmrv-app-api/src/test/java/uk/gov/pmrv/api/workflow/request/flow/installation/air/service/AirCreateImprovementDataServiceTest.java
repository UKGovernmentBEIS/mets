package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import jakarta.validation.Validator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.ActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.AppliedStandard;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.measuredemissions.MeasurementOfCO2MeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.measuredemissions.MeasurementOfN2OMeasuredEmissionsTier;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamDescription;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationPFC;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementMeasurement;


@ExtendWith(MockitoExtension.class)
class AirCreateImprovementDataServiceTest {

    @InjectMocks
    private AirCreateImprovementDataService service;
    
    @Mock
    private Validator validator;

    @Test
    void createImprovementData() {

        final Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder().sourceStreams(List.of(
                SourceStream.builder()
                    .id("ss1")
                    .description(SourceStreamDescription.ACETYLENE)
                    .reference("ss ref 1")
                    .build(),
                SourceStream.builder()
                    .id("ss2")
                    .description(SourceStreamDescription.BIOGASOLINE)
                    .reference("ss ref 2")
                    .build()
            )).build())
            .emissionSources(EmissionSources.builder().emissionSources(List.of(
                EmissionSource.builder().id("es1").description("es desc 1").reference("es ref 1").build(),
                EmissionSource.builder().id("es2").description("es desc 2").reference("es ref 2").build()
            )).build())
            .emissionPoints(EmissionPoints.builder().emissionPoints(List.of(
                    EmissionPoint.builder().id("ep1").description("ep desc 1").reference("ep ref 1").build(),
                    EmissionPoint.builder().id("ep2").description("ep desc 2").reference("ep ref 2").build()
                )
            ).build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(
                Map.of(
                    MonitoringApproachType.CALCULATION_CO2, createCalculationCO2(),
                    MonitoringApproachType.CALCULATION_PFC, createCalculationPFC(),
                    MonitoringApproachType.MEASUREMENT_CO2, createMeasurementCO2(),
                    MonitoringApproachType.MEASUREMENT_N2O, createMeasurementN2O(),
                    MonitoringApproachType.FALLBACK, createFallback()
                )).build())
            .build();

        final List<AirImprovement> improvementData = service.createImprovementData(permit);

        final List<AirImprovement> expected = List.of(

            AirImprovementMeasurement.builder()
                .type(MonitoringApproachType.MEASUREMENT_CO2)
                .categoryType(CategoryType.MINOR)
                .sourceStreamReferences(List.of("ss ref 1: Acetylene", "ss ref 2: Biogasoline"))
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .emissionPoint("ep ref 1: ep desc 1")
                .parameter("Some parameter")
                .tier("Tier 1")
                .build(),

            AirImprovementMeasurement.builder()
                .type(MonitoringApproachType.MEASUREMENT_N2O)
                .categoryType(CategoryType.MINOR)
                .sourceStreamReferences(List.of("ss ref 1: Acetylene"))
                .emissionSources(List.of("es ref 1: es desc 1"))
                .emissionPoint("ep ref 1: ep desc 1")
                .parameter("Some parameter")
                .tier("Tier 1")
                .build(),

            AirImprovementFallback.builder()
                .type(MonitoringApproachType.FALLBACK)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .build(),

            AirImprovementCalculationPFC.builder()
                .type(MonitoringApproachType.CALCULATION_PFC)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of( "es ref 2: es desc 2"))
                .emissionPoints(List.of("ep ref 2: ep desc 2"))
                .parameter("Activity data")
                .tier("Tier 1")
                .build(),

            AirImprovementCalculationPFC.builder()
                .type(MonitoringApproachType.CALCULATION_PFC)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of( "es ref 2: es desc 2"))
                .emissionPoints(List.of("ep ref 2: ep desc 2"))
                .parameter("Emission factor")
                .tier("Tier 2")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Emission factor")
                .tier("Tier 1")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Activity data")
                .tier("Tier 2")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Net calorific value")
                .tier("Tier 1")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Oxidation factor")
                .tier("Tier 2")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Carbon content")
                .tier("Tier 1")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Conversion factor")
                .tier("Tier 2")
                .build(),

            AirImprovementCalculationCO2.builder()
                .type(MonitoringApproachType.CALCULATION_CO2)
                .categoryType(CategoryType.MAJOR)
                .sourceStreamReference("ss ref 1: Acetylene")
                .emissionSources(List.of("es ref 1: es desc 1", "es ref 2: es desc 2"))
                .parameter("Biomass fraction")
                .tier("Tier 2")
                .build()
        );

        assertThat(improvementData).containsExactlyInAnyOrderElementsOf(expected);

        verify(validator, times(1)).validate(improvementData);
    }


    private MeasurementOfCO2MonitoringApproach createMeasurementCO2() {

        final Set<String> sourceStreams = new LinkedHashSet<>();
        sourceStreams.add("ss1");
        sourceStreams.add("ss2");
        
        final Set<String> emissionSources = new LinkedHashSet<>();
        emissionSources.add("es1");
        emissionSources.add("es2");

        return MeasurementOfCO2MonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT_CO2)
            .emissionPointCategoryAppliedTiers(List.of(
                MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                    .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                        .emissionPoint("ep1")
                        .sourceStreams(sourceStreams)
                        .categoryType(CategoryType.MINOR)
                        .emissionSources(emissionSources)
                        .build())
                    .appliedStandard(AppliedStandard.builder()
                        .parameter("Some parameter")
                        .build())
                    .measuredEmissions(MeasurementOfCO2MeasuredEmissions.builder()
                        .tier(MeasurementOfCO2MeasuredEmissionsTier.TIER_1)
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .build())
                    .build(),
                MeasurementOfCO2EmissionPointCategoryAppliedTier.builder()
                    .emissionPointCategory(MeasurementOfCO2EmissionPointCategory.builder()
                        .categoryType(CategoryType.DE_MINIMIS)
                        .build())
                    .appliedStandard(AppliedStandard.builder()
                        .parameter("Some parameter")
                        .build())
                    .build()
            ))
            .build();
    }

    private MeasurementOfN2OMonitoringApproach createMeasurementN2O() {

        final Set<String> sourceStreams = new LinkedHashSet<>();
        sourceStreams.add("ss1");

        final Set<String> emissionSources = new LinkedHashSet<>();
        emissionSources.add("es1");

        return MeasurementOfN2OMonitoringApproach.builder()
            .type(MonitoringApproachType.MEASUREMENT_N2O)
            .emissionPointCategoryAppliedTiers(List.of(
                MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                    .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                        .emissionPoint("ep1")
                        .sourceStreams(sourceStreams)
                        .categoryType(CategoryType.MINOR)
                        .emissionSources(emissionSources)
                        .build())
                    .measuredEmissions(MeasurementOfN2OMeasuredEmissions.builder()
                        .tier(MeasurementOfN2OMeasuredEmissionsTier.TIER_1)
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .build())
                    .appliedStandard(AppliedStandard.builder()
                        .parameter("Some parameter")
                        .build())
                    .build(),
                MeasurementOfN2OEmissionPointCategoryAppliedTier.builder()
                    .emissionPointCategory(MeasurementOfN2OEmissionPointCategory.builder()
                        .categoryType(CategoryType.MINOR)
                        .build())
                    .measuredEmissions(MeasurementOfN2OMeasuredEmissions.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.TRUE)
                            .build())
                        .build())
                    .appliedStandard(AppliedStandard.builder()
                        .parameter("Some parameter")
                        .build())
                    .build()
            ))
            .build();
    }

    private CalculationOfCO2MonitoringApproach createCalculationCO2() {
        
        final Set<String> emissionSources = new LinkedHashSet<>();
        emissionSources.add("es1");
        emissionSources.add("es2");
        
        return CalculationOfCO2MonitoringApproach.builder()
            .type(MonitoringApproachType.CALCULATION_CO2)
            .sourceStreamCategoryAppliedTiers(List.of(
                CalculationSourceStreamCategoryAppliedTier.builder()
                    .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                        .sourceStream("ss1")
                        .emissionSources(emissionSources)
                        .categoryType(CategoryType.MAJOR)
                        .build())
                    .activityData(CalculationActivityData.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationActivityDataTier.TIER_2)
                        .build())
                    .netCalorificValue(CalculationNetCalorificValue.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationNetCalorificValueTier.TIER_1).build())
                    .emissionFactor(CalculationEmissionFactor.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationEmissionFactorTier.TIER_1).build())
                    .oxidationFactor(CalculationOxidationFactor.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationOxidationFactorTier.TIER_2).build())
                    .carbonContent(CalculationCarbonContent.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationCarbonContentTier.TIER_1).build())
                    .conversionFactor(CalculationConversionFactor.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationConversionFactorTier.TIER_2).build())
                    .biomassFraction(CalculationBiomassFraction.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(Boolean.FALSE)
                            .build())
                        .tier(CalculationBiomassFractionTier.TIER_2)
                        .build()
                    ).build())).build();
    }

    private FallbackMonitoringApproach createFallback() {

        final Set<String> emissionSources = new LinkedHashSet<>();
        emissionSources.add("es1");
        emissionSources.add("es2");
        
        return FallbackMonitoringApproach.builder()
            .type(MonitoringApproachType.FALLBACK)
            .sourceStreamCategoryAppliedTiers(List.of(
                FallbackSourceStreamCategoryAppliedTier.builder()
                    .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                        .sourceStream("ss1")
                        .emissionSources(emissionSources)
                        .categoryType(CategoryType.MAJOR)
                        .build())
                    .build(),
                FallbackSourceStreamCategoryAppliedTier.builder()
                    .sourceStreamCategory(FallbackSourceStreamCategory.builder()
                        .sourceStream("ss1")
                        .emissionSources(emissionSources)
                        .categoryType(CategoryType.DE_MINIMIS)
                        .build())
                    .build()
            ))
            .build();
    }

    private CalculationOfPFCMonitoringApproach createCalculationPFC() {

        return CalculationOfPFCMonitoringApproach.builder()
            .type(MonitoringApproachType.CALCULATION_PFC)
            .sourceStreamCategoryAppliedTiers(List.of(
                PFCSourceStreamCategoryAppliedTier.builder()
                    .activityData(PFCActivityData.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(false)
                            .build())
                        .tier(ActivityDataTier.TIER_1)
                        .build())
                    .emissionFactor(PFCEmissionFactor.builder()
                        .highestRequiredTier(HighestRequiredTier.builder()
                            .isHighestRequiredTier(false)
                            .build())
                        .tier(PFCEmissionFactorTier.TIER_2)
                        .build())
                    .sourceStreamCategory(PFCSourceStreamCategory.builder()
                        .sourceStream("ss1")
                        .emissionSources(Set.of("es2"))
                        .categoryType(CategoryType.MAJOR)
                        .emissionPoints(Set.of("ep2"))
                        .build())
                    .build()
            )).build();
    }

}

package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.CalculationOfPFCMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationpfc.PFCSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2EmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementco2.MeasurementOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OEmissionPointCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.measurementn2o.MeasurementOfN2OMonitoringApproach;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationCO2;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementCalculationPFC;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementFallback;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.AirImprovementMeasurement;
import uk.gov.pmrv.api.workflow.request.flow.installation.air.domain.CalculationParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AirCreateImprovementDataService {
    
    private final Validator validator;
    
    public List<AirImprovement> createImprovementData(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        improvements.addAll(this.createCalculationCO2Improvements(permit));
        improvements.addAll(this.createCalculationPFCImprovements(permit));
        improvements.addAll(this.createMeasurementCO2Improvements(permit));
        improvements.addAll(this.createMeasurementN2OImprovements(permit));
        improvements.addAll(this.createFallbackImprovements(permit));

        validator.validate(improvements);
        
        return improvements;
    }
    
    private List<AirImprovement> createCalculationCO2Improvements(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        final CalculationOfCO2MonitoringApproach calculation = (CalculationOfCO2MonitoringApproach)
            permit.getMonitoringApproaches().getMonitoringApproaches().get(MonitoringApproachType.CALCULATION_CO2);
        if (calculation != null) {
            for (CalculationSourceStreamCategoryAppliedTier tier : calculation.getSourceStreamCategoryAppliedTiers()) {

                final CategoryType categoryType = tier.getSourceStreamCategory().getCategoryType();
                if (!List.of(CategoryType.MAJOR, CategoryType.MINOR).contains(categoryType)) {
                    continue;
                }
                final String stream = this.getStream(tier.getSourceStreamCategory().getSourceStream(), permit);
                final List<String> emissionSources =
                    this.getEmissionSources(tier.getSourceStreamCategory().getEmissionSources(), permit);

                if (tier.getEmissionFactor().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getEmissionFactor().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.EF.getDescription())
                        .tier(tier.getEmissionFactor().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getOxidationFactor().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getOxidationFactor().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.OF.getDescription())
                        .tier(tier.getOxidationFactor().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getActivityData().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getActivityData().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.AD.getDescription())
                        .tier(tier.getActivityData().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getCarbonContent().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getCarbonContent().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.CC.getDescription())
                        .tier(tier.getCarbonContent().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getNetCalorificValue().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getNetCalorificValue().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.NCV.getDescription())
                        .tier(tier.getNetCalorificValue().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getConversionFactor().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getConversionFactor().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.CF.getDescription())
                        .tier(tier.getConversionFactor().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getBiomassFraction().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getBiomassFraction().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationCO2 improvement = AirImprovementCalculationCO2.builder()
                        .type(MonitoringApproachType.CALCULATION_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .parameter(CalculationParameter.BF.getDescription())
                        .tier(tier.getBiomassFraction().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }
            }
        }
        return improvements;
    }

    private List<AirImprovement> createCalculationPFCImprovements(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        final CalculationOfPFCMonitoringApproach pfc = (CalculationOfPFCMonitoringApproach)
            permit.getMonitoringApproaches().getMonitoringApproaches().get(MonitoringApproachType.CALCULATION_PFC);
        if (pfc != null) {
            for (PFCSourceStreamCategoryAppliedTier tier : pfc.getSourceStreamCategoryAppliedTiers()) {

                final CategoryType categoryType = tier.getSourceStreamCategory().getCategoryType();
                if (!List.of(CategoryType.MAJOR, CategoryType.MINOR).contains(categoryType)) {
                    continue;
                }
                final String stream = this.getStream(tier.getSourceStreamCategory().getSourceStream(), permit);
                final List<String> emissionSources =
                    this.getEmissionSources(tier.getSourceStreamCategory().getEmissionSources(), permit);
                final List<String> emissionPoints = tier.getSourceStreamCategory().getEmissionPoints().stream()
                    .map(ep -> this.getEmissionPoint(ep, permit)).toList();

                if (tier.getEmissionFactor().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getEmissionFactor().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationPFC improvement = AirImprovementCalculationPFC.builder()
                        .type(MonitoringApproachType.CALCULATION_PFC)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .emissionPoints(emissionPoints)
                        .parameter(CalculationParameter.EF.getDescription())
                        .tier(tier.getEmissionFactor().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }

                if (tier.getActivityData().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getActivityData().getHighestRequiredTier().getIsHighestRequiredTier())) {

                    final AirImprovementCalculationPFC improvement = AirImprovementCalculationPFC.builder()
                        .type(MonitoringApproachType.CALCULATION_PFC)
                        .categoryType(categoryType)
                        .sourceStreamReference(stream)
                        .emissionSources(emissionSources)
                        .emissionPoints(emissionPoints)
                        .parameter(CalculationParameter.AD.getDescription())
                        .tier(tier.getActivityData().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }
            }
        }
        return improvements;
    }

    private List<AirImprovement> createMeasurementCO2Improvements(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        final MeasurementOfCO2MonitoringApproach co2 = (MeasurementOfCO2MonitoringApproach)
            permit.getMonitoringApproaches().getMonitoringApproaches().get(MonitoringApproachType.MEASUREMENT_CO2);
        if (co2 != null) {
            for (MeasurementOfCO2EmissionPointCategoryAppliedTier tier : co2.getEmissionPointCategoryAppliedTiers()) {

                final CategoryType categoryType = tier.getEmissionPointCategory().getCategoryType();
                if (!List.of(CategoryType.MAJOR, CategoryType.MINOR).contains(categoryType)) {
                    continue;
                }
                final List<String> streams = tier.getEmissionPointCategory().getSourceStreams().stream()
                    .map(s -> this.getStream(s, permit)).toList();
                final List<String> emissionSources =
                    this.getEmissionSources(tier.getEmissionPointCategory().getEmissionSources(), permit);
                final String emissionPoint = this.getEmissionPoint(tier.getEmissionPointCategory().getEmissionPoint(),
                    permit);
                final String parameter = tier.getAppliedStandard().getParameter();

                if (tier.getMeasuredEmissions().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getMeasuredEmissions().getHighestRequiredTier().getIsHighestRequiredTier())
                ) {
                    final AirImprovementMeasurement improvement = AirImprovementMeasurement.builder()
                        .type(MonitoringApproachType.MEASUREMENT_CO2)
                        .categoryType(categoryType)
                        .sourceStreamReferences(streams)
                        .emissionSources(emissionSources)
                        .emissionPoint(emissionPoint)
                        .parameter(parameter)
                        .tier(tier.getMeasuredEmissions().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }
            }
        }
        return improvements;
    }

    private List<AirImprovement> createMeasurementN2OImprovements(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        final MeasurementOfN2OMonitoringApproach n2o = (MeasurementOfN2OMonitoringApproach)
            permit.getMonitoringApproaches().getMonitoringApproaches().get(MonitoringApproachType.MEASUREMENT_N2O);
        if (n2o != null) {
            for (MeasurementOfN2OEmissionPointCategoryAppliedTier tier : n2o.getEmissionPointCategoryAppliedTiers()) {

                final CategoryType categoryType = tier.getEmissionPointCategory().getCategoryType();
                if (!List.of(CategoryType.MAJOR, CategoryType.MINOR).contains(categoryType)) {
                    continue;
                }
                final List<String> streams = tier.getEmissionPointCategory().getSourceStreams().stream()
                    .map(s -> this.getStream(s, permit)).toList();
                final List<String> emissionSources =
                    this.getEmissionSources(tier.getEmissionPointCategory().getEmissionSources(), permit);
                final String emissionPoint = this.getEmissionPoint(tier.getEmissionPointCategory().getEmissionPoint(),
                    permit);
                final String parameter = tier.getAppliedStandard().getParameter();

                if (tier.getMeasuredEmissions().getHighestRequiredTier() != null &&
                    Boolean.FALSE.equals(tier.getMeasuredEmissions().getHighestRequiredTier().getIsHighestRequiredTier())
                ) {
                    final AirImprovementMeasurement improvement = AirImprovementMeasurement.builder()
                        .type(MonitoringApproachType.MEASUREMENT_N2O)
                        .categoryType(categoryType)
                        .sourceStreamReferences(streams)
                        .emissionSources(emissionSources)
                        .emissionPoint(emissionPoint)
                        .parameter(parameter)
                        .tier(tier.getMeasuredEmissions().getTier().getDescription())
                        .build();

                    improvements.add(improvement);
                }
            }
        }
        return improvements;
    }

    private List<AirImprovement> createFallbackImprovements(final Permit permit) {
        
        final List<AirImprovement> improvements = new ArrayList<>();
        
        final FallbackMonitoringApproach fallback = (FallbackMonitoringApproach)
            permit.getMonitoringApproaches().getMonitoringApproaches().get(MonitoringApproachType.FALLBACK);
        if (fallback != null) {
            for (FallbackSourceStreamCategoryAppliedTier tier : fallback.getSourceStreamCategoryAppliedTiers()) {

                final CategoryType categoryType = tier.getSourceStreamCategory().getCategoryType();
                if (!List.of(CategoryType.MAJOR, CategoryType.MINOR).contains(categoryType)) {
                    continue;
                }

                final String stream = this.getStream(tier.getSourceStreamCategory().getSourceStream(), permit);
                final List<String> emissionSources =
                    this.getEmissionSources(tier.getSourceStreamCategory().getEmissionSources(), permit);

                final AirImprovementFallback improvement = AirImprovementFallback.builder()
                    .type(MonitoringApproachType.FALLBACK)
                    .categoryType(categoryType)
                    .sourceStreamReference(stream)
                    .emissionSources(emissionSources)
                    .build();

                improvements.add(improvement);
            }
        }
        return improvements;
    }

    private String getStream(final String sourceStreamId, final Permit permit) {

        return permit.getSourceStreams().getSourceStreams().stream()
            .filter(sourceStream -> sourceStream.getId().equals(sourceStreamId))
            .findFirst()
            .map(sourceStream -> sourceStream.getReference() + ": " + sourceStream.getDescription().getDescription())
            .orElse(null);
    }

    private List<String> getEmissionSources(final Set<String> emissionSourceIds, final Permit permit) {

        return emissionSourceIds.stream().map(
            es -> {
                final EmissionSource emissionSource = permit.getEmissionSources().getEmissionSources().stream()
                    .filter(e -> e.getId().equals(es)).findFirst().orElse(EmissionSource.builder().build());
                return emissionSource.getReference() + ": " + emissionSource.getDescription();
            }
        ).toList();
    }

    private String getEmissionPoint(final String emissionPointId, final Permit permit) {

        return permit.getEmissionPoints().getEmissionPoints().stream()
            .filter(emissionPoint -> emissionPoint.getId().equals(emissionPointId))
            .findFirst()
            .map(emissionPoint -> emissionPoint.getReference() + ": " + emissionPoint.getDescription())
            .orElse(null);
    }
}

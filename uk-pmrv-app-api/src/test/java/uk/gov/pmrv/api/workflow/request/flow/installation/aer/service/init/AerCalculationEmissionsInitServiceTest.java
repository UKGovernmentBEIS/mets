package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service.init;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityData;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationActivityDataTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFraction;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationBiomassFractionTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContent;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationCarbonContentTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationConversionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationEmissionFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValue;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationNetCalorificValueTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOfCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactorTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategory;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.CategoryType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.AerMonitoringApproachEmissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationBiomassFractionMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationCarbonContentMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationConversionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOfCO2Emissions;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOxidationFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationParameterMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationSourceStreamEmission;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;

class AerCalculationEmissionsInitServiceTest {

    private final AerCalculationEmissionsInitService service = new AerCalculationEmissionsInitService();

    @ParameterizedTest
    @MethodSource("provideInputSourceStreamTypesAndExpectedCalculationParameterMonitoringTiers")
    void initialize(SourceStreamType sourceStreamType, CalculationBiomassFractionTier biomassFractionTier,
                    List<CalculationParameterMonitoringTier> expectedCalculationParameterMonitoringTiers) {
        String sourceStreamId = "SS0001";
        SourceStream permitSourceStream = SourceStream.builder().id(sourceStreamId).type(sourceStreamType).build();

        boolean biomassFractionExistsInPermit = biomassFractionTier != null;
        CalculationSourceStreamCategoryAppliedTier calculationSourceStreamCategoryAppliedTier =
            CalculationSourceStreamCategoryAppliedTier.builder()
                .sourceStreamCategory(CalculationSourceStreamCategory.builder()
                    .sourceStream(sourceStreamId)
                    .emissionSources(Set.of("ES0001"))
                    .categoryType(CategoryType.MAJOR)
                    .annualEmittedCO2Tonnes(BigDecimal.valueOf(2000))
                    .build()
                )
                .activityData(CalculationActivityData.builder().tier(CalculationActivityDataTier.TIER_3).build())
                .conversionFactor(CalculationConversionFactor.builder().exist(false).build())
                .carbonContent(CalculationCarbonContent.builder().tier(CalculationCarbonContentTier.TIER_2A).build())
                .emissionFactor(CalculationEmissionFactor.builder().tier(CalculationEmissionFactorTier.TIER_1).build())
                .oxidationFactor(CalculationOxidationFactor.builder().tier(CalculationOxidationFactorTier.TIER_2).build())
                .netCalorificValue(CalculationNetCalorificValue.builder().tier(CalculationNetCalorificValueTier.TIER_2B).build())
                .biomassFraction(CalculationBiomassFraction.builder().exist(biomassFractionExistsInPermit).tier(biomassFractionTier).build())
                .build();

        Permit permit = Permit.builder()
            .sourceStreams(SourceStreams.builder()
                .sourceStreams(List.of(permitSourceStream))
                .build()
            )
            .monitoringApproaches(MonitoringApproaches.builder()
                .monitoringApproaches(
                    Map.of(MonitoringApproachType.CALCULATION_CO2, CalculationOfCO2MonitoringApproach.builder()
                        .hasTransfer(true)
                        .sourceStreamCategoryAppliedTiers(List.of(calculationSourceStreamCategoryAppliedTier))
                        .build())
                )
                .build()
            )
            .build();

        AerMonitoringApproachEmissions aerMonitoringApproachEmissions = service.initialize(permit);

        //assertions
        assertNotNull(aerMonitoringApproachEmissions);
        assertEquals(MonitoringApproachType.CALCULATION_CO2, aerMonitoringApproachEmissions.getType());

        assertThat(aerMonitoringApproachEmissions).isInstanceOf(CalculationOfCO2Emissions.class);

        CalculationOfCO2Emissions calculationEmissions = (CalculationOfCO2Emissions) aerMonitoringApproachEmissions;
        List<CalculationSourceStreamEmission> calcSourceStreamEmissions =
            calculationEmissions.getSourceStreamEmissions();
        assertThat(calcSourceStreamEmissions).hasSize(1);

        CalculationSourceStreamEmission calcSourceStreamEmission = calcSourceStreamEmissions.get(0);

        assertEquals(sourceStreamId, calcSourceStreamEmission.getSourceStream());
        assertNotNull(calcSourceStreamEmission.getId());
        assertEquals(calculationSourceStreamCategoryAppliedTier.getSourceStreamCategory().getEmissionSources(),
            calcSourceStreamEmission.getEmissionSources());
        assertThat(calcSourceStreamEmission.getParameterMonitoringTiers())
            .containsExactlyInAnyOrderElementsOf(expectedCalculationParameterMonitoringTiers);

        DurationRange calcSourceStreamDurationRange =
            calcSourceStreamEmission.getDurationRange();
        assertNotNull(calcSourceStreamDurationRange);
        assertTrue(calcSourceStreamDurationRange.getFullYearCovered());

        BiomassPercentages calcSourceStreamBiomassPercentages =
            calcSourceStreamEmission.getBiomassPercentages();
        assertNotNull(calcSourceStreamBiomassPercentages);
        assertEquals(biomassFractionExistsInPermit, calcSourceStreamBiomassPercentages.getContains());
    }

    @Test
    void getMonitoringApproachType() {
        assertEquals(MonitoringApproachType.CALCULATION_CO2, service.getMonitoringApproachType());
    }

    private static Stream<Arguments> provideInputSourceStreamTypesAndExpectedCalculationParameterMonitoringTiers() {
        return Stream.of(
            //sourceStreamType of category1
            Arguments.of(SourceStreamType.COMBUSTION_SOLID_FUELS,
                CalculationBiomassFractionTier.TIER_2,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build(),
                    CalculationEmissionFactorMonitoringTier.builder().type(CalculationParameterType.EMISSION_FACTOR).tier(CalculationEmissionFactorTier.TIER_1).build(),
                    CalculationOxidationFactorMonitoringTier.builder().type(CalculationParameterType.OXIDATION_FACTOR).tier(CalculationOxidationFactorTier.TIER_2).build(),
                    CalculationBiomassFractionMonitoringTier.builder().type(CalculationParameterType.BIOMASS_FRACTION).tier(CalculationBiomassFractionTier.TIER_2).build())
            ),
            //sourceStreamType of category2
            Arguments.of(SourceStreamType.COKE_MASS_BALANCE,
                null,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build(),
                    CalculationCarbonContentMonitoringTier.builder().type(CalculationParameterType.CARBON_CONTENT).tier(CalculationCarbonContentTier.TIER_2A).build())
            ),
            //sourceStreamType of category4
            Arguments.of(SourceStreamType.CEMENT_CLINKER_CKD,
                null,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationEmissionFactorMonitoringTier.builder().type(CalculationParameterType.EMISSION_FACTOR).tier(CalculationEmissionFactorTier.TIER_1).build())
            ),
            //sourceStreamType of category5
            Arguments.of(SourceStreamType.REFINERIES_CATALYTIC_CRACKER_REGENERATION,
                null,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build())
            ),
            //sourceStreamType of category6
            Arguments.of(SourceStreamType.COKE_FUEL_AS_PROCESS_INPUT,
                CalculationBiomassFractionTier.NO_TIER,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE).tier(CalculationNetCalorificValueTier.TIER_2B).build(),
                    CalculationEmissionFactorMonitoringTier.builder().type(CalculationParameterType.EMISSION_FACTOR).tier(CalculationEmissionFactorTier.TIER_1).build(),
                    CalculationBiomassFractionMonitoringTier.builder().type(CalculationParameterType.BIOMASS_FRACTION).tier(CalculationBiomassFractionTier.NO_TIER).build())
            ),
            //sourceStreamType of category7
            Arguments.of(SourceStreamType.METAL_ORE_CARBONATE_INPUT,
                null,
                List.of(CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                    CalculationConversionFactorMonitoringTier.builder().type(CalculationParameterType.CONVERSION_FACTOR).build(),
                    CalculationEmissionFactorMonitoringTier.builder().type(CalculationParameterType.EMISSION_FACTOR).tier(CalculationEmissionFactorTier.TIER_1)
                        .build())
            ),
            //sourceStreamType of null category
            Arguments.of(SourceStreamType.OTHER, null, List.of(
                CalculationActivityDataMonitoringTier.builder().type(CalculationParameterType.ACTIVITY_DATA).tier(CalculationActivityDataTier.TIER_3).build(),
                CalculationNetCalorificValueMonitoringTier.builder().type(CalculationParameterType.NET_CALORIFIC_VALUE)
                    .tier(CalculationNetCalorificValueTier.TIER_2B).build(),
                CalculationEmissionFactorMonitoringTier.builder().type(CalculationParameterType.EMISSION_FACTOR).tier(CalculationEmissionFactorTier.TIER_1)
                    .build(),
                CalculationOxidationFactorMonitoringTier.builder().type(CalculationParameterType.OXIDATION_FACTOR).tier(CalculationOxidationFactorTier.TIER_2)
                    .build()))
        );
    }
}
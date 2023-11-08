package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation;

import org.junit.jupiter.api.Test;
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
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationOxidationFactor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationParameterType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2.CalculationSourceStreamCategoryAppliedTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationActivityDataMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationBiomassFractionMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationCarbonContentMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationConversionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationEmissionFactorMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationNetCalorificValueMonitoringTier;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation.CalculationOxidationFactorMonitoringTier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculationParameterTypeTest {

    @Test
    void getParameterTier() {
        CalculationActivityDataTier calculationActivityDataTier = CalculationActivityDataTier.TIER_3;
        CalculationNetCalorificValueTier calculationNetCalorificValueTier = CalculationNetCalorificValueTier.NO_TIER;
        CalculationEmissionFactorTier calculationEmissionFactorTier = CalculationEmissionFactorTier.TIER_2B;
        CalculationConversionFactorTier calculationConversionFactorTier = CalculationConversionFactorTier.TIER_1;
        CalculationCarbonContentTier calculationCarbonContentTier = CalculationCarbonContentTier.TIER_2A;
        CalculationBiomassFractionTier calculationBiomassFractionTier = CalculationBiomassFractionTier.TIER_3;

        CalculationSourceStreamCategoryAppliedTier calculationSourceStreamCategoryAppliedTier =
            CalculationSourceStreamCategoryAppliedTier.builder()
                .activityData(CalculationActivityData.builder().tier(calculationActivityDataTier).build())
                .netCalorificValue(CalculationNetCalorificValue.builder().tier(calculationNetCalorificValueTier).build())
                .emissionFactor(CalculationEmissionFactor.builder().tier(calculationEmissionFactorTier).build())
                .conversionFactor(CalculationConversionFactor.builder().tier(calculationConversionFactorTier).build())
                .carbonContent(CalculationCarbonContent.builder().tier(calculationCarbonContentTier).build())
                .oxidationFactor(CalculationOxidationFactor.builder().exist(false).build())
                .biomassFraction(CalculationBiomassFraction.builder().tier(calculationBiomassFractionTier).build())
                .build();

        CalculationActivityDataMonitoringTier calculationActivityDataMonitoringTier =
            CalculationActivityDataMonitoringTier.builder()
                .type(CalculationParameterType.ACTIVITY_DATA)
                .tier(calculationActivityDataTier)
                .build();
        CalculationNetCalorificValueMonitoringTier calculationNetCalorificValueMonitoringTier =
            CalculationNetCalorificValueMonitoringTier.builder()
                .type(CalculationParameterType.NET_CALORIFIC_VALUE)
                .tier(calculationNetCalorificValueTier)
                .build();
        CalculationEmissionFactorMonitoringTier calculationEmissionFactorMonitoringTier =
            CalculationEmissionFactorMonitoringTier.builder()
                .type(CalculationParameterType.EMISSION_FACTOR)
                .tier(calculationEmissionFactorTier)
                .build();
        CalculationConversionFactorMonitoringTier calculationConversionFactorMonitoringTier =
            CalculationConversionFactorMonitoringTier.builder()
                .type(CalculationParameterType.CONVERSION_FACTOR)
                .tier(calculationConversionFactorTier)
                .build();
        CalculationCarbonContentMonitoringTier calculationCarbonContentMonitoringTier =
            CalculationCarbonContentMonitoringTier.builder()
                .type(CalculationParameterType.CARBON_CONTENT)
                .tier(calculationCarbonContentTier)
                .build();
        CalculationOxidationFactorMonitoringTier calculationOxidationFactorMonitoringTier =
            CalculationOxidationFactorMonitoringTier.builder()
            .type(CalculationParameterType.OXIDATION_FACTOR)
            .build();
        CalculationBiomassFractionMonitoringTier calculationBiomassFractionMonitoringTier =
            CalculationBiomassFractionMonitoringTier.builder()
                .type(CalculationParameterType.BIOMASS_FRACTION)
                .tier(calculationBiomassFractionTier)
                .build();

        assertEquals(calculationActivityDataMonitoringTier,
            CalculationParameterType.ACTIVITY_DATA.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationNetCalorificValueMonitoringTier,
            CalculationParameterType.NET_CALORIFIC_VALUE.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationEmissionFactorMonitoringTier,
            CalculationParameterType.EMISSION_FACTOR.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationConversionFactorMonitoringTier,
            CalculationParameterType.CONVERSION_FACTOR.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationCarbonContentMonitoringTier,
            CalculationParameterType.CARBON_CONTENT.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationOxidationFactorMonitoringTier,
            CalculationParameterType.OXIDATION_FACTOR.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
        assertEquals(calculationBiomassFractionMonitoringTier,
            CalculationParameterType.BIOMASS_FRACTION.getParameterMonitoringTier(calculationSourceStreamCategoryAppliedTier));
    }
}
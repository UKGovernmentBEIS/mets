package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint1;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint2;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProductType;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.SpecialProductDataSourceValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class SpecialProductDataSourceValidatorTest {

    @InjectMocks
    private SpecialProductDataSourceValidator validator;


    @Test
    void validateDataSources_Aromatics_validFirstDataSource() {
        Map<AnnexIIPoint2,AnnexVIISection44> details= new HashMap<>();
        details.put(AnnexIIPoint2.PARAXYLENE_PRODUCTION, AnnexVIISection44.INDIRECT_DETERMINATION);

        AromaticsQuantitiesOfSupplementalFieldDataSource dataSource =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();
        AromaticsSP aromaticsSP = AromaticsSP.builder().specialProductType(SpecialProductType.AROMATICS)
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.PARAXYLENE_PRODUCTION))
                .dataSources(List.of(dataSource)).build();
        assertTrue(validator.validateDataSources(aromaticsSP));
    }

    @Test
    void validateDataSources_Aromatics_invalidFirstDataSource() {
        Map<AnnexIIPoint2,AnnexVIISection44> details= new HashMap<>();
        details.put(AnnexIIPoint2.PARAXYLENE_PRODUCTION, null);

        AromaticsQuantitiesOfSupplementalFieldDataSource dataSource =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();
        AromaticsSP aromaticsSP = AromaticsSP.builder().specialProductType(SpecialProductType.AROMATICS)
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.PARAXYLENE_PRODUCTION))
                .dataSources(List.of(dataSource)).build();
        assertFalse(validator.validateDataSources(aromaticsSP));
    }

    @Test
    void validateDataSources_Aromatics_validAdditionalDataSources() {
        final Map<AnnexIIPoint2,AnnexVIISection44> firstDatasourceValuesMap = new HashMap<>();
        firstDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,AnnexVIISection44.METHOD_MONITORING_PLAN);
        firstDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,AnnexVIISection44.INDIRECT_DETERMINATION);

        //The other datasources must have at least one non empty AnnexVIISection44 value
        final Map<AnnexIIPoint2,AnnexVIISection44> secondDatasourceValuesMap = new HashMap<>();
        secondDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,AnnexVIISection44.INDIRECT_DETERMINATION);
        secondDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,null);

        final Map<AnnexIIPoint2,AnnexVIISection44> thirdDatasourceValuesMap = new HashMap<>();
        thirdDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,AnnexVIISection44.METHOD_MONITORING_PLAN);
        thirdDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,AnnexVIISection44.INDIRECT_DETERMINATION);

        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceFirst =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(firstDatasourceValuesMap)
                        .build();
        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceSecond =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("2")
                        .details(secondDatasourceValuesMap)
                        .build();
        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceThird =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("3")
                        .details(thirdDatasourceValuesMap)
                        .build();
        AromaticsSP aromaticsSP = AromaticsSP.builder().specialProductType(SpecialProductType.AROMATICS)
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.HYDRODEALKYLATION,AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION))
                .dataSources(List.of(dataSourceFirst,dataSourceSecond,dataSourceThird)).build();
        assertTrue(validator.validateDataSources(aromaticsSP));
    }

    @Test
    void validateDataSources_Aromatics_invalidAdditionalDataSources() {
        final Map<AnnexIIPoint2,AnnexVIISection44> firstDatasourceValuesMap = new HashMap<>();
        firstDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,AnnexVIISection44.METHOD_MONITORING_PLAN);
        firstDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,AnnexVIISection44.INDIRECT_DETERMINATION);

        //The other datasources must have at least one non empty AnnexVIISection44 value
        final Map<AnnexIIPoint2,AnnexVIISection44> secondDatasourceValuesMap = new HashMap<>();
        secondDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,null);
        secondDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,null);

        final Map<AnnexIIPoint2,AnnexVIISection44> thirdDatasourceValuesMap = new HashMap<>();
        thirdDatasourceValuesMap.put(AnnexIIPoint2.HYDRODEALKYLATION,AnnexVIISection44.METHOD_MONITORING_PLAN);
        thirdDatasourceValuesMap.put(AnnexIIPoint2.AROMATIC_SOLVENT_EXTRACTION,AnnexVIISection44.INDIRECT_DETERMINATION);

        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceFirst =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(firstDatasourceValuesMap)
                        .build();
        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceSecond =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("2")
                        .details(secondDatasourceValuesMap)
                        .build();
        AromaticsQuantitiesOfSupplementalFieldDataSource dataSourceThird =
                AromaticsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("3")
                        .details(thirdDatasourceValuesMap)
                        .build();
        AromaticsSP aromaticsSP = AromaticsSP.builder().specialProductType(SpecialProductType.AROMATICS)
                .relevantCWTFunctions(Set.of(AnnexIIPoint2.PARAXYLENE_PRODUCTION))
                .dataSources(List.of(dataSourceFirst,dataSourceSecond,dataSourceThird)).build();
        assertFalse(validator.validateDataSources(aromaticsSP));
    }


    @Test
    void validateDataSources_RefineryProducts_validFirstDataSource() {
        Map<AnnexIIPoint1,AnnexVIISection44> details= new HashMap<>();
        details.put(AnnexIIPoint1.ASPHALT_MANUFACTURE, AnnexVIISection44.INDIRECT_DETERMINATION);

        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSource =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();
        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder().specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE))
                .refineryProductsDataSources(List.of(dataSource)).build();
        assertTrue(validator.validateDataSources(refineryProductsSP));
    }

    @Test
    void validateDataSources_RefineryProducts_invalidFirstDataSource() {
        Map<AnnexIIPoint1,AnnexVIISection44> details= new HashMap<>();
        details.put(AnnexIIPoint1.ASPHALT_MANUFACTURE, null);

        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSource =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();
        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder().specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE))
                .refineryProductsDataSources(List.of(dataSource)).build();
        assertFalse(validator.validateDataSources(refineryProductsSP));
    }

    @Test
    void validateDataSources_RefineryProducts_invalidAdditionalDataSource() {
        //The first datasource must have all the values selected
        final Map<AnnexIIPoint1,AnnexVIISection44> firstDatasourceValuesMap = new HashMap<>();
        firstDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexVIISection44.METHOD_MONITORING_PLAN);
        firstDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,AnnexVIISection44.INDIRECT_DETERMINATION);

        //The other datasources must have at least one non empty AnnexVIISection44 value
        final Map<AnnexIIPoint1,AnnexVIISection44> secondDatasourceValuesMap = new HashMap<>();
        secondDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,null);
        secondDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,null);

        final Map<AnnexIIPoint1,AnnexVIISection44> thirdDatasourceValuesMap = new HashMap<>();
        thirdDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexVIISection44.METHOD_MONITORING_PLAN);
        thirdDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,AnnexVIISection44.INDIRECT_DETERMINATION);

        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceFirst =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(firstDatasourceValuesMap)
                        .build();
        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceSecond =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("2")
                        .details(secondDatasourceValuesMap)
                        .build();
        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceThird =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("3")
                        .details(thirdDatasourceValuesMap)
                        .build();

        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder().specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE))
                .refineryProductsDataSources(List.of(dataSourceFirst,dataSourceSecond,dataSourceThird)).build();
        assertFalse(validator.validateDataSources(refineryProductsSP));
    }

    @Test
    void validateDataSources_RefineryProducts_validAdditionalDataSource() {
        //The first datasource must have all the values selected
        final Map<AnnexIIPoint1,AnnexVIISection44> firstDatasourceValuesMap = new HashMap<>();
        firstDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexVIISection44.METHOD_MONITORING_PLAN);
        firstDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,AnnexVIISection44.INDIRECT_DETERMINATION);

        //The other datasources must have at least one non empty AnnexVIISection44 value
        final Map<AnnexIIPoint1,AnnexVIISection44> secondDatasourceValuesMap = new HashMap<>();
        secondDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexVIISection44.METHOD_MONITORING_PLAN);
        secondDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,null);

        final Map<AnnexIIPoint1,AnnexVIISection44> thirdDatasourceValuesMap = new HashMap<>();
        thirdDatasourceValuesMap.put(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexVIISection44.METHOD_MONITORING_PLAN);
        thirdDatasourceValuesMap.put(AnnexIIPoint1.AIR_SEPARATION,AnnexVIISection44.INDIRECT_DETERMINATION);

        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceFirst =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("1")
                        .details(firstDatasourceValuesMap)
                        .build();
        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceSecond =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("2")
                        .details(secondDatasourceValuesMap)
                        .build();
        RefineryProductsQuantitiesOfSupplementalFieldDataSource dataSourceThird =
                RefineryProductsQuantitiesOfSupplementalFieldDataSource.builder()
                        .dataSourceNo("3")
                        .details(thirdDatasourceValuesMap)
                        .build();

        RefineryProductsSP refineryProductsSP = RefineryProductsSP.builder().specialProductType(SpecialProductType.REFINERY_PRODUCTS)
                .refineryProductsRelevantCWTFunctions(Set.of(AnnexIIPoint1.ASPHALT_MANUFACTURE,AnnexIIPoint1.AIR_SEPARATION))
                .refineryProductsDataSources(List.of(dataSourceFirst,dataSourceSecond,dataSourceThird)).build();
        assertTrue(validator.validateDataSources(refineryProductsSP));
    }

    @Test
    void validateDataSources_Hydrogen_validFirstDataSource() {
        HydrogenDetails details = HydrogenDetails.builder().
                volumeFraction(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        HydrogenDataSource dataSource =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .dataSources(List.of(dataSource))
                .build();

        assertTrue(validator.validateDataSources(hydrogenSP));
    }

    @Test
    void validateDataSources_Hydrogen_invalidFirstDataSource() {
        HydrogenDetails details = HydrogenDetails.builder().
                volumeFraction(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(null).build();

        HydrogenDataSource dataSource =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .dataSources(List.of(dataSource))
                .build();

        assertFalse(validator.validateDataSources(hydrogenSP));
    }

    @Test
    void validateDataSources_Hydrogen_validAdditionalDataSource() {
        HydrogenDetails details = HydrogenDetails.builder().
                volumeFraction(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        HydrogenDataSource dataSource =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        HydrogenDataSource dataSource2 =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .dataSources(List.of(dataSource, dataSource2))
                .build();

        assertTrue(validator.validateDataSources(hydrogenSP));
    }

    @Test
    void validateDataSources_Hydrogen_invalidAdditionalDataSource() {
        HydrogenDetails details = HydrogenDetails.builder().
                volumeFraction(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        HydrogenDetails details2 = HydrogenDetails.builder().
                volumeFraction(null)
                .totalProduction(null).build();

        HydrogenDataSource dataSource =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        HydrogenDataSource dataSource2 =
                HydrogenDataSource.builder()
                        .dataSourceNo("1")
                        .details(details2)
                        .build();

        HydrogenSP hydrogenSP = HydrogenSP.builder()
                .specialProductType(SpecialProductType.HYDROGEN)
                .dataSources(List.of(dataSource, dataSource2))
                .build();

        assertFalse(validator.validateDataSources(hydrogenSP));
    }

    @Test
    void validateDataSources_SynthesisGas_validFirstDataSource() {
        SynthesisGasDetails details = SynthesisGasDetails.builder()
                .compositionData(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        SynthesisGasDataSource dataSource =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        SynthesisGasSP synthesisGasSP = SynthesisGasSP.builder()
                .specialProductType(SpecialProductType.SYNTHESIS_GAS)
                .dataSources(List.of(dataSource))
                .build();

        assertTrue(validator.validateDataSources(synthesisGasSP));
    }

    @Test
    void validateDataSources_SynthesisGas_invalidFirstDataSource() {
        SynthesisGasDetails details = SynthesisGasDetails.builder()
                .compositionData(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(null).build();

        SynthesisGasDataSource dataSource =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        SynthesisGasSP synthesisGasSP = SynthesisGasSP.builder()
                .specialProductType(SpecialProductType.SYNTHESIS_GAS)
                .dataSources(List.of(dataSource))
                .build();

        assertFalse(validator.validateDataSources(synthesisGasSP));
    }

    @Test
    void validateDataSources_SynthesisGas_validAdditionalDataSource() {
        SynthesisGasDetails details = SynthesisGasDetails.builder()
                .compositionData(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        SynthesisGasDataSource dataSource =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        SynthesisGasDataSource dataSource2 =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        SynthesisGasSP synthesisGasSP = SynthesisGasSP.builder()
                .specialProductType(SpecialProductType.SYNTHESIS_GAS)
                .dataSources(List.of(dataSource, dataSource2))
                .build();

        assertTrue(validator.validateDataSources(synthesisGasSP));
    }

    @Test
    void validateDataSources_SynthesisGas_invalidAdditionalDataSource() {
        SynthesisGasDetails details = SynthesisGasDetails.builder()
                .compositionData(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .totalProduction(AnnexVIISection44.METHOD_MONITORING_PLAN).build();

        SynthesisGasDetails details2 = SynthesisGasDetails.builder()
                .compositionData(null)
                .totalProduction(null).build();

        SynthesisGasDataSource dataSource =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details)
                        .build();

        SynthesisGasDataSource dataSource2 =
                SynthesisGasDataSource.builder()
                        .dataSourceNo("1")
                        .details(details2)
                        .build();

        SynthesisGasSP synthesisGasSP = SynthesisGasSP.builder()
                .specialProductType(SpecialProductType.SYNTHESIS_GAS)
                .dataSources(List.of(dataSource, dataSource2))
                .build();

        assertFalse(validator.validateDataSources(synthesisGasSP));
    }

}

package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputDataSourceFA;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.FuelInputAndRelevantEmissionFactorFADatasourceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FuelInputAndRelevantEmissionFactorFADatasourceValidatorTest {

    @InjectMocks
    private FuelInputAndRelevantEmissionFactorFADatasourceValidator validator;


    @Test
    void validateDataSources_validFirstDataSource() {
        final FuelInputDataSourceFA firstDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61)
                .emissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE)
                .wasteGasNetCalorificValue(AnnexVIISection46.CONSTANT_VALUES_STANDARD_SUPPLIER)
                .wasteGasFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL).build();
        List<FuelInputDataSourceFA> dataSources = List.of(firstDataSource);

        final FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA =
                FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(true).dataSources(dataSources).build();

        assertTrue(validator.validateDataSources(fuelInputAndRelevantEmissionFactorFA));
    }

    @Test
    void validateDataSources_invalidFirstDataSource() {
        final FuelInputDataSourceFA firstDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(null)
                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61)
                .build();

        List<FuelInputDataSourceFA> dataSources = List.of(firstDataSource);
        final FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA =
                FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(true).dataSources(dataSources).build();
        assertFalse(validator.validateDataSources(fuelInputAndRelevantEmissionFactorFA));
    }

    @Test
    void validateDataSources_validAdditionalDataSource() {
        final FuelInputDataSourceFA firstDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61)
                .emissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE)
                .wasteGasNetCalorificValue(AnnexVIISection46.CONSTANT_VALUES_STANDARD_SUPPLIER)
                .wasteGasFuelInput(AnnexVIISection44.LEGAL_METROLOGICAL_CONTROL).build();
        final FuelInputDataSourceFA secondDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("2").fuelInput(AnnexVIISection44.METHOD_MONITORING_PLAN)
                .weightedEmissionFactor(null)
                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61)
                .emissionFactor(AnnexVIISection46.CONSTANT_VALUES_SCIENTIFIC_EVIDENCE)
                .wasteGasNetCalorificValue(AnnexVIISection46.CONSTANT_VALUES_STANDARD_SUPPLIER)
                .wasteGasNetCalorificValue(AnnexVIISection46.SIMPLIFIED_LABORATORY_ANALYSES_SECTION_62).build();
        List<FuelInputDataSourceFA> dataSources = List.of(firstDataSource,secondDataSource);
        final FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA =
                FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(true).dataSources(dataSources).build();
        assertTrue(validator.validateDataSources(fuelInputAndRelevantEmissionFactorFA));

    }

    @Test
    void validateDataSources_invalidAdditionalDataSource() {
        final FuelInputDataSourceFA firstDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN)
                .netCalorificValue(AnnexVIISection46.LABORATORY_ANALYSES_SECTION_61).build();
        final FuelInputDataSourceFA secondDataSource = FuelInputDataSourceFA.builder()
                .fuelInputDataSourceNo("2").fuelInput(null)
                .weightedEmissionFactor(null).netCalorificValue(null).build();
        List<FuelInputDataSourceFA> dataSources = List.of(firstDataSource,secondDataSource);
        final FuelInputAndRelevantEmissionFactorFA fuelInputAndRelevantEmissionFactorFA =
                FuelInputAndRelevantEmissionFactorFA.builder().wasteGasesInput(false).dataSources(dataSources).build();
        assertFalse(validator.validateDataSources(fuelInputAndRelevantEmissionFactorFA));
    }
}


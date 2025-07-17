package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection44;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection46;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputDataSourcePB;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.FuelInputAndRelevantEmissionFactorPBDatasourceValidator;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class FuelInputAndRelevantEmissionFactorPBDatasourceValidatorTest {

    @InjectMocks
    private FuelInputAndRelevantEmissionFactorPBDatasourceValidator validator;

    @Test
    void validateDataSources_validFirstDataSource() {
        final FuelInputDataSourcePB firstDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).build();

        List<FuelInputDataSourcePB> dataSources = List.of(firstDataSource);

        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB =
                FuelInputAndRelevantEmissionFactorPB.builder().exist(true).dataSources(dataSources).build();
        assertTrue(validator.validateDataSources(fuelInputAndRelevantEmissionFactorPB));
    }

    @Test
    void validateDataSources_invalidFirstDataSource() {
        final FuelInputDataSourcePB firstDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(null).build();

        List<FuelInputDataSourcePB> dataSources = List.of(firstDataSource);
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB =
                FuelInputAndRelevantEmissionFactorPB.builder().exist(true).dataSources(dataSources).build();
        assertFalse(validator.validateDataSources(fuelInputAndRelevantEmissionFactorPB));
    }

    @Test
    void validateDataSources_validAdditionalDataSource() {
        final FuelInputDataSourcePB firstDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).build();
        final FuelInputDataSourcePB secondDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("2").fuelInput(AnnexVIISection44.METHOD_MONITORING_PLAN)
                .weightedEmissionFactor(null).build();
        List<FuelInputDataSourcePB> dataSources = List.of(firstDataSource,secondDataSource);
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB =
                FuelInputAndRelevantEmissionFactorPB.builder().exist(true).dataSources(dataSources).build();
        assertTrue(validator.validateDataSources(fuelInputAndRelevantEmissionFactorPB));

    }

    @Test
    void validateDataSources_invalidAdditionalDataSource_returnsFalse() {
        final FuelInputDataSourcePB firstDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("1").fuelInput(AnnexVIISection44.INDIRECT_DETERMINATION)
                .weightedEmissionFactor(AnnexVIISection46.CALCULATION_METHOD_MONITORING_PLAN).build();
        final FuelInputDataSourcePB secondDataSource = FuelInputDataSourcePB.builder()
                .fuelInputDataSourceNo("2").fuelInput(null)
                .weightedEmissionFactor(null).build();
        List<FuelInputDataSourcePB> dataSources = List.of(firstDataSource,secondDataSource);
        FuelInputAndRelevantEmissionFactorPB fuelInputAndRelevantEmissionFactorPB =
                FuelInputAndRelevantEmissionFactorPB.builder().exist(true).dataSources(dataSources).build();
        assertFalse(validator.validateDataSources(fuelInputAndRelevantEmissionFactorPB));
    }
}


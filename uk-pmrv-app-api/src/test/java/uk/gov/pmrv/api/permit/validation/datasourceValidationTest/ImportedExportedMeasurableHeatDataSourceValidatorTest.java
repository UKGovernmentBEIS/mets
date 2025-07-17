package uk.gov.pmrv.api.permit.validation.datasourceValidationTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.AnnexVIISection72;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.SubInstallationHierarchicalOrder;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatEnergyFlowDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatType;
import uk.gov.pmrv.api.permit.validation.datasourceValidation.ImportedExportedMeasurableHeatDataSourceValidator;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class ImportedExportedMeasurableHeatDataSourceValidatorTest {

    @InjectMocks
    private ImportedExportedMeasurableHeatDataSourceValidator validator;

    @Test
    void validateDataSources_validFirstDataSource() {
        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSource =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("1")
                        .netMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS)
                        .build();

        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_IMPORTED))
                .measurableHeatImportedFromPulp(false)
                .pulpMethodologyDeterminationDescription("description")
                .methodologyDeterminationDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .dataSources(List.of(dataSource))
                .build();

        assertTrue(validator.validateDataSources(importedExportedMeasurableHeat));
    }

    @Test
    void validateDataSources_invalidFirstDataSource() {
        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSource =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("1")
                        .build();

        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_IMPORTED))
                .measurableHeatImportedFromPulp(false)
                .pulpMethodologyDeterminationDescription("description")
                .methodologyDeterminationDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .dataSources(List.of(dataSource))
                .build();

        assertFalse(validator.validateDataSources(importedExportedMeasurableHeat));
    }

    @Test
    void validateDataSources_validAdditionalDataSources() {
        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSourceFirst =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("1")
                        .netMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS)
                        .build();

        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSourceSecond =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("2")
                        .netMeasurableHeatFlows(AnnexVIISection72.DOCUMENTATION)
                        .build();

        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_IMPORTED))
                .measurableHeatImportedFromPulp(false)
                .pulpMethodologyDeterminationDescription("description")
                .methodologyDeterminationDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .dataSources(List.of(dataSourceFirst,dataSourceSecond))
                .build();

        assertTrue(validator.validateDataSources(importedExportedMeasurableHeat));
    }

    @Test
    void validateDataSources_invalidAdditionalDataSources() {
        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSourceFirst =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("1")
                        .netMeasurableHeatFlows(AnnexVIISection72.MEASUREMENTS)
                        .build();

        ImportedExportedMeasurableHeatEnergyFlowDataSource dataSourceSecond =
                ImportedExportedMeasurableHeatEnergyFlowDataSource.builder()
                        .energyFlowDataSourceNo("2")
                        .build();

        ImportedExportedMeasurableHeat importedExportedMeasurableHeat = ImportedExportedMeasurableHeat.builder()
                .fuelBurnCalculationTypes(Set.of(ImportedExportedMeasurableHeatType.MEASURABLE_HEAT_IMPORTED))
                .measurableHeatImportedFromPulp(false)
                .pulpMethodologyDeterminationDescription("description")
                .methodologyDeterminationDescription("description")
                .hierarchicalOrder(SubInstallationHierarchicalOrder.builder().followed(true).build())
                .dataSources(List.of(dataSourceFirst,dataSourceSecond))
                .build();

        assertFalse(validator.validateDataSources(importedExportedMeasurableHeat));
    }

}

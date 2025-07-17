package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeatEnergyFlowDataSource;

import java.util.Comparator;
import java.util.List;

@Component
public class ImportedExportedMeasurableHeatDataSourceValidator implements DatasourceValidator<ImportedExportedMeasurableHeat> {
    @Override
    public boolean validateDataSources(ImportedExportedMeasurableHeat subInstallationMember) {
        List<ImportedExportedMeasurableHeatEnergyFlowDataSource> dataSources = subInstallationMember.getDataSources();

        List<ImportedExportedMeasurableHeatEnergyFlowDataSource> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(ImportedExportedMeasurableHeatEnergyFlowDataSource::getEnergyFlowDataSourceNo)).toList();

        return isFirstDataSourceValid(sortedDataSources.getFirst()) && dataSources.stream().skip(1).allMatch(this::isAdditionalDataSourceValid);
    }

    private boolean isFirstDataSourceValid(ImportedExportedMeasurableHeatEnergyFlowDataSource dataSource) {
        if (ObjectUtils.isEmpty(dataSource)) {
            return false;
        }

        return !ObjectUtils.isEmpty(dataSource.getNetMeasurableHeatFlows());
    }

    private boolean isAdditionalDataSourceValid(ImportedExportedMeasurableHeatEnergyFlowDataSource dataSource) {
        return !( ObjectUtils.isEmpty(dataSource.getNetMeasurableHeatFlows()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatExported()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatImported()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatPulp()) &&
                ObjectUtils.isEmpty(dataSource.getMeasurableHeatNitricAcid()));
    }
}

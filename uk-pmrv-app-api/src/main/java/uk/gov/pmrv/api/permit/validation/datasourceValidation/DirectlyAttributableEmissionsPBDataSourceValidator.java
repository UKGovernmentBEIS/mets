package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.ImportedExportedAmountsDataSource;

import java.util.Comparator;
import java.util.List;

@Component
public class DirectlyAttributableEmissionsPBDataSourceValidator implements DatasourceValidator<DirectlyAttributableEmissionsPB> {
    @Override
    public boolean validateDataSources(DirectlyAttributableEmissionsPB subInstallationMember) {
        List<ImportedExportedAmountsDataSource> dataSources = subInstallationMember.getDataSources();

        //number of data sources is being validated on domain level. if empty, return true by default.
        if (ObjectUtils.isEmpty(dataSources)) {
            return true;
        }

        List<ImportedExportedAmountsDataSource> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(ImportedExportedAmountsDataSource::getImportedExportedAmountsDataSourceNo)).toList();

        ImportedExportedAmountsDataSource firstDataSource = sortedDataSources.getFirst();
        return !invalidFirstDatasource(firstDataSource) &&
                !invalidAdditionalDataSources(sortedDataSources);
    }

    private static boolean invalidFirstDatasource(ImportedExportedAmountsDataSource firstDataSource) {
        return ObjectUtils.isEmpty(firstDataSource.getAmounts());
    }

    private static boolean invalidAdditionalDataSources(List<ImportedExportedAmountsDataSource> dataSources) {
        return dataSources.stream().skip(1)
                .anyMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getAmounts()) &&
                                ObjectUtils.isEmpty(dataSource.getBiomassContent()) &&
                                ObjectUtils.isEmpty(dataSource.getEnergyContent()) &&
                                ObjectUtils.isEmpty(dataSource.getEmissionFactorOrCarbonContent()));
    }
}

package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExportedDataSource;

import java.util.Comparator;
import java.util.List;

@Component
public class MeasurableHeatExportedDataSourceValidator implements DatasourceValidator<MeasurableHeatExported> {


    @Override
    public boolean validateDataSources(MeasurableHeatExported subInstallationMember) {
        if(!subInstallationMember.isMeasurableHeatExported()) {
            return true;
        }

        List<MeasurableHeatExportedDataSource> dataSources = subInstallationMember.getDataSources();

        List<MeasurableHeatExportedDataSource> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(MeasurableHeatExportedDataSource::getDataSourceNo)).toList();

        MeasurableHeatExportedDataSource firstDataSource = sortedDataSources.getFirst();
        return !invalidFirstDatasource(firstDataSource) &&
                !invalidAdditionalDataSources(sortedDataSources);
    }

    private static boolean invalidFirstDatasource(MeasurableHeatExportedDataSource firstDataSource) {
        return ObjectUtils.isEmpty(firstDataSource.getNetMeasurableHeatFlows());
    }

    private static boolean invalidAdditionalDataSources(List<MeasurableHeatExportedDataSource> dataSources) {

        if(dataSources.size() <= 1) {
            return false;
        }
        return dataSources.stream()
                .anyMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getHeatExported()) &&
                                ObjectUtils.isEmpty(dataSource.getNetMeasurableHeatFlows()));
    }
}

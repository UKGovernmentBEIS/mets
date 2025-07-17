package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedDataSourceDetails;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImportedType;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Component
public class MeasurableHeatImportedDataSourceValidator implements DatasourceValidator<MeasurableHeatImported> {

    @Override
    public boolean validateDataSources(MeasurableHeatImported subInstallationMember) {
        List<MeasurableHeatImportedDataSource> dataSources = subInstallationMember.getDataSources();

        if(subInstallationMember.getMeasurableHeatImportedActivities().contains(MeasurableHeatImportedType.MEASURABLE_HEAT_NO_MEASURABLE_HEAT_IMPORTED)) {
            return true;
        }

        List<MeasurableHeatImportedDataSource> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(MeasurableHeatImportedDataSource::getDataSourceNo)).toList();

        MeasurableHeatImportedDataSource firstDataSource = sortedDataSources.getFirst();
        return !invalidFirstDatasource(firstDataSource) &&
                !invalidAdditionalDataSources(sortedDataSources)
                && !invalidActivities(subInstallationMember);
    }

    private static boolean invalidFirstDatasource(MeasurableHeatImportedDataSource firstDataSource) {
        return firstDataSource.getMeasurableHeatImportedActivityDetails().values().stream().anyMatch(MeasurableHeatImportedDataSourceValidator::isMeasurableHeatImportedDataSourceDetailsNotFullyPopulated);
    }

    private static boolean invalidAdditionalDataSources(List<MeasurableHeatImportedDataSource> dataSources) {
        if(dataSources.size() <= 1) {
            return false;
        }
        return dataSources.stream()
                .anyMatch(dataSource ->
                        dataSource.getMeasurableHeatImportedActivityDetails()
                                .values()
                                .stream()
                                .allMatch(MeasurableHeatImportedDataSourceValidator::isMeasurableHeatImportedDataSourceDetailsEmpty)
                );}

    private static boolean isMeasurableHeatImportedDataSourceDetailsNotFullyPopulated(MeasurableHeatImportedDataSourceDetails details) {
        return ObjectUtils.isEmpty(details.getEntity()) || ObjectUtils.isEmpty(details.getNetContent());
    }

    private static boolean isMeasurableHeatImportedDataSourceDetailsEmpty(MeasurableHeatImportedDataSourceDetails details) {
        return ObjectUtils.isEmpty(details.getEntity()) && ObjectUtils.isEmpty(details.getNetContent());
    }

    private static boolean invalidActivities(MeasurableHeatImported subInstallationMember) {
        List<MeasurableHeatImportedDataSource> dataSources = subInstallationMember.getDataSources();
        return dataSources.stream().anyMatch(dataSource -> !subInstallationMember.getMeasurableHeatImportedActivities().containsAll(dataSource.getMeasurableHeatImportedActivityDetails().keySet()));
    }

}

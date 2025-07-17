package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputDataSourcePB;

import java.util.Comparator;
import java.util.List;

@Component
public class FuelInputAndRelevantEmissionFactorPBDatasourceValidator implements DatasourceValidator<FuelInputAndRelevantEmissionFactorPB> {

    @Override
    public boolean validateDataSources(FuelInputAndRelevantEmissionFactorPB subInstallationMember) {
        List<FuelInputDataSourcePB> dataSources = subInstallationMember.getDataSources();

        //number of data sources is being validated on domain level. if empty, return true by default.
        if(ObjectUtils.isEmpty(dataSources)) {
            return true;
        }

        List<FuelInputDataSourcePB> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(FuelInputDataSourcePB::getFuelInputDataSourceNo)).toList();

        FuelInputDataSourcePB firstDataSource = sortedDataSources.getFirst();

        return !invalidFirstDatasource(firstDataSource) && !invalidAdditionalDataSources(dataSources);

    }

    private static boolean invalidFirstDatasource(FuelInputDataSourcePB firstDataSource) {
        return ObjectUtils.isEmpty(firstDataSource.getFuelInput()) ||
                ObjectUtils.isEmpty(firstDataSource.getWeightedEmissionFactor());
    }

    private static boolean invalidAdditionalDataSources(List<FuelInputDataSourcePB> dataSources) {
        if(dataSources.size() <= 1) {
            return false;
        }
        return dataSources.stream().skip(1)
                .anyMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getFuelInput()) &&
                                ObjectUtils.isEmpty(dataSource.getWeightedEmissionFactor()));

    }


}

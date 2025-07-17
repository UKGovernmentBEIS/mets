package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputDataSourceFA;

import java.util.Comparator;
import java.util.List;

@Component
public class FuelInputAndRelevantEmissionFactorFADatasourceValidator implements DatasourceValidator<FuelInputAndRelevantEmissionFactorFA> {


    @Override
    public boolean validateDataSources(FuelInputAndRelevantEmissionFactorFA subInstallationMember) {
        List<FuelInputDataSourceFA> dataSources = subInstallationMember.getDataSources();

        //number of data sources is being validated on domain level. if empty, return true by default.
        if(ObjectUtils.isEmpty(dataSources)) {
            return true;
        }

        List<FuelInputDataSourceFA> sortedDataSources = dataSources.stream()
                .sorted(Comparator.comparing(FuelInputDataSourceFA::getFuelInputDataSourceNo)).toList();

        FuelInputDataSourceFA firstDataSource = sortedDataSources.getFirst();
        return !invalidFirstDatasource(firstDataSource,subInstallationMember.getWasteGasesInput()) &&
                !invalidAdditionalDataSources(sortedDataSources,subInstallationMember.getWasteGasesInput());
    }

    private static boolean invalidFirstDatasource(FuelInputDataSourceFA firstDataSource,boolean wasteGas) {
        if(wasteGas) {
            return ObjectUtils.isEmpty(firstDataSource.getFuelInput()) ||
                    ObjectUtils.isEmpty(firstDataSource.getWeightedEmissionFactor()) ||
                    ObjectUtils.isEmpty(firstDataSource.getNetCalorificValue()) ||
                    ObjectUtils.isEmpty(firstDataSource.getEmissionFactor()) ||
                    ObjectUtils.isEmpty(firstDataSource.getWasteGasFuelInput()) ||
                    ObjectUtils.isEmpty(firstDataSource.getWasteGasNetCalorificValue());
        }
        else {
            return ObjectUtils.isEmpty(firstDataSource.getFuelInput()) ||
                    ObjectUtils.isEmpty(firstDataSource.getWeightedEmissionFactor()) ||
                    ObjectUtils.isEmpty(firstDataSource.getNetCalorificValue()) ||

                    ObjectUtils.isNotEmpty(firstDataSource.getEmissionFactor()) ||
                    ObjectUtils.isNotEmpty(firstDataSource.getWasteGasFuelInput()) ||
                    ObjectUtils.isNotEmpty(firstDataSource.getWasteGasNetCalorificValue());
        }
    }

    private static boolean invalidAdditionalDataSources(List<FuelInputDataSourceFA> dataSources,boolean wasteGas) {
        if(dataSources.size() <= 1) {
            return false;
        }
        if(wasteGas) {
            return dataSources.stream().skip(1)
                    .anyMatch(dataSource ->
                            ObjectUtils.isEmpty(dataSource.getFuelInput()) &&
                                    ObjectUtils.isEmpty(dataSource.getWeightedEmissionFactor()) &&
                                    ObjectUtils.isEmpty(dataSource.getNetCalorificValue()) &&
                                    ObjectUtils.isEmpty(dataSource.getEmissionFactor()) &&
                                    ObjectUtils.isEmpty(dataSource.getWasteGasFuelInput()) &&
                                    ObjectUtils.isEmpty(dataSource.getWasteGasNetCalorificValue()));
        }
        else {
            return dataSources.stream().skip(1)
                    .anyMatch(dataSource ->
                            ObjectUtils.isEmpty(dataSource.getFuelInput()) &&
                                    ObjectUtils.isEmpty(dataSource.getWeightedEmissionFactor()) &&
                                    ObjectUtils.isEmpty(dataSource.getNetCalorificValue()));
        }
    }

}

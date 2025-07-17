package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.*;

import java.util.List;

@Component
public class AnnualActivityLevelDataSourceValidator implements DatasourceValidator<AnnualActivityLevel>{
    @Override
    public boolean validateDataSources(AnnualActivityLevel subInstallationMember) {
        return switch (subInstallationMember.getAnnualLevelType()) {
            case ACTIVITY_HEAT -> validateAnnualActivityHeat((AnnualActivityHeatLevel) subInstallationMember);
            case ACTIVITY_FUEL -> validateAnnualActivityFuel((AnnualActivityFuelLevel) subInstallationMember);
            default ->
                    throw new IllegalArgumentException("Invalid annual level type for validation of data sources" +
                            ": " + subInstallationMember.getAnnualLevelType());
        };
    }

    private boolean validateAnnualActivityHeat(AnnualActivityHeatLevel subInstallationMember) {
        List<MeasurableHeatFlow> dataSources = subInstallationMember.getMeasurableHeatFlowList();
        MeasurableHeatFlow firstDataSource = dataSources.getFirst();

        if (ObjectUtils.isEmpty(firstDataSource.getNet()) || ObjectUtils.isEmpty(firstDataSource.getQuantification())) {
            return false;
        }
        if (dataSources.size() == 1)
            return true;

        return dataSources.stream().skip(1)
                .noneMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getNet()) &&
                                ObjectUtils.isEmpty(dataSource.getQuantification()));
    }

    private boolean validateAnnualActivityFuel(AnnualActivityFuelLevel subInstallationMember) {
        List<ActivityFuelDataSource> dataSources = subInstallationMember.getFuelDataSources();
        ActivityFuelDataSource firstDataSource = dataSources.getFirst();

        if (ObjectUtils.isEmpty(firstDataSource.getFuelInput())) {
            return false;
        }
        if (dataSources.size() == 1)
            return true;

        return dataSources.stream().skip(1)
                .noneMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getFuelInput()) &&
                                ObjectUtils.isEmpty(dataSource.getEnergyContent()));
    }
}

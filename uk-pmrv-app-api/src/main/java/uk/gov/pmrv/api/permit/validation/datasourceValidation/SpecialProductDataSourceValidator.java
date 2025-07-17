package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint1;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.AnnexIIPoint2;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.aromatics.AromaticsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.hydrogen.HydrogenSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsQuantitiesOfSupplementalFieldDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.refineryproducts.RefineryProductsSP;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasDataSource;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.synthesisgas.SynthesisGasSP;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class SpecialProductDataSourceValidator implements DatasourceValidator<SpecialProduct>{

    @Override
    public boolean validateDataSources(SpecialProduct subInstallationMember) {
        switch (subInstallationMember.getSpecialProductType()) {
            case REFINERY_PRODUCTS -> {
                return validateRefineryProduct((RefineryProductsSP) subInstallationMember);
            }
            case AROMATICS -> {
                return validateAromatics((AromaticsSP) subInstallationMember);
            }
            case HYDROGEN -> {
                return validateHydrogen((HydrogenSP) subInstallationMember);
            }
            case SYNTHESIS_GAS -> {
                return validateSynthesisGas((SynthesisGasSP) subInstallationMember);
            }
            default -> throw new IllegalArgumentException("Invalid special product type for validation of data sources" +
                    ": " + subInstallationMember.getSpecialProductType());
        }
    }

    private boolean validateRefineryProduct(RefineryProductsSP subInstallationMember) {
        List<RefineryProductsQuantitiesOfSupplementalFieldDataSource> dataSources = subInstallationMember.getRefineryProductsDataSources();
        dataSources = dataSources.stream().
                        sorted(Comparator.comparing(RefineryProductsQuantitiesOfSupplementalFieldDataSource::getDataSourceNo))
                        .toList();

        RefineryProductsQuantitiesOfSupplementalFieldDataSource firstDataSource = dataSources.getFirst();
        if (firstDataSource.getDetails().values().stream().anyMatch(Objects::isNull)) {
            return false;
        }
        if (dataSources.stream().skip(1).anyMatch(dataSource ->
                dataSource.getDetails().values().stream().allMatch(Objects::isNull))) {
            return false;
        }

        Set<AnnexIIPoint1> relevantCWTFunctions = subInstallationMember.getRefineryProductsRelevantCWTFunctions();
        // Stream through each data source, validating keySet equality
        return subInstallationMember.getRefineryProductsDataSources().stream().allMatch(dataSource ->
                dataSource.getDetails().keySet().equals(relevantCWTFunctions));
    }

    private boolean validateAromatics(AromaticsSP subInstallationMember) {
        List<AromaticsQuantitiesOfSupplementalFieldDataSource> dataSources = subInstallationMember.getDataSources();
        dataSources = dataSources.stream().
                sorted(Comparator.comparing(AromaticsQuantitiesOfSupplementalFieldDataSource::getDataSourceNo))
                .toList();
        AromaticsQuantitiesOfSupplementalFieldDataSource firstDataSource = dataSources.getFirst();
        if (firstDataSource.getDetails().values().stream().anyMatch(Objects::isNull)) {
            return false;
        }
        if(dataSources.stream().skip(1).anyMatch(dataSource ->
                dataSource.getDetails().values().stream().allMatch(Objects::isNull))) {
            return false;
        }

        Set<AnnexIIPoint2> relevantCWTFunctions = subInstallationMember.getRelevantCWTFunctions();
        // Stream through each data source, validating keySet equality
        return subInstallationMember.getDataSources().stream().allMatch(dataSource ->
                dataSource.getDetails().keySet().equals(relevantCWTFunctions));
    }

    private boolean validateHydrogen(HydrogenSP subInstallationMember) {
        List<HydrogenDataSource> dataSources = subInstallationMember.getDataSources();
        dataSources = dataSources.stream().sorted(Comparator.comparing(HydrogenDataSource::getDataSourceNo))
                .toList();
        HydrogenDataSource firstDataSource = dataSources.getFirst();
        if (ObjectUtils.isEmpty(firstDataSource.getDetails().getTotalProduction()) || ObjectUtils.isEmpty(firstDataSource.getDetails().getVolumeFraction())) {
            return false;
        }
        if (dataSources.size() == 1)
            return true;

        return dataSources.stream().skip(1)
                .noneMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getDetails().getTotalProduction()) &&
                                ObjectUtils.isEmpty(dataSource.getDetails().getVolumeFraction()));
    }

    private boolean validateSynthesisGas(SynthesisGasSP subInstallationMember) {
        List<SynthesisGasDataSource> dataSources = subInstallationMember.getDataSources();
        dataSources = dataSources.stream().sorted(Comparator.comparing(SynthesisGasDataSource::getDataSourceNo))
                .toList();
        SynthesisGasDataSource firstDataSource = dataSources.getFirst();
        if (ObjectUtils.isEmpty(firstDataSource.getDetails().getTotalProduction()) || ObjectUtils.isEmpty(firstDataSource.getDetails().getCompositionData())) {
            return false;
        }
        if (dataSources.size() == 1)
            return true;

        return dataSources.stream().skip(1)
                .noneMatch(dataSource ->
                        ObjectUtils.isEmpty(dataSource.getDetails().getTotalProduction()) &&
                                ObjectUtils.isEmpty(dataSource.getDetails().getCompositionData()));
    }
}

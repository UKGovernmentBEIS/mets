package uk.gov.pmrv.api.permit.validation.datasourceValidation;

import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.directlyattributableemissions.DirectlyAttributableEmissionsPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.annuallevels.annualactivitylevels.AnnualActivityLevel;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorFA;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.fuelinputandrelevantemissionsfactor.FuelInputAndRelevantEmissionFactorPB;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.importedexportedmeasurableheat.ImportedExportedMeasurableHeat;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.exported.MeasurableHeatExported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.measurableheat.imported.MeasurableHeatImported;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.subinstallations.specialproduct.SpecialProduct;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceValidatorFactory {

    private final Map<Class<?>, DatasourceValidator<?>> validatorMap = new HashMap<>();


    public DataSourceValidatorFactory(
            FuelInputAndRelevantEmissionFactorPBDatasourceValidator pbDatasourceValidator,
            FuelInputAndRelevantEmissionFactorFADatasourceValidator faDatasourceValidator,
            SpecialProductDataSourceValidator specialProductDataSourceValidator,
            MeasurableHeatImportedDataSourceValidator measurableHeatImportedDataSourceValidator,
            ImportedExportedMeasurableHeatDataSourceValidator importedExportedMeasurableHeatDataSourceValidator,
            DirectlyAttributableEmissionsPBDataSourceValidator directlyAttributableEmissionsPBDataSourceValidator,
            MeasurableHeatExportedDataSourceValidator measurableHeatExportedDataSourceValidator,
            AnnualActivityLevelDataSourceValidator annualActivityLevelDataSourceValidator) {
        validatorMap.put(FuelInputAndRelevantEmissionFactorPB.class, pbDatasourceValidator);
        validatorMap.put(FuelInputAndRelevantEmissionFactorFA.class, faDatasourceValidator);
        validatorMap.put(SpecialProduct.class, specialProductDataSourceValidator);
        validatorMap.put(MeasurableHeatImported.class, measurableHeatImportedDataSourceValidator);
        validatorMap.put(MeasurableHeatExported.class, measurableHeatExportedDataSourceValidator);
        validatorMap.put(ImportedExportedMeasurableHeat.class, importedExportedMeasurableHeatDataSourceValidator);
        validatorMap.put(AnnualActivityLevel.class, annualActivityLevelDataSourceValidator);
        validatorMap.put(DirectlyAttributableEmissionsPB.class, directlyAttributableEmissionsPBDataSourceValidator);
    }

    @SuppressWarnings("unchecked")
    public <T> DatasourceValidator<T> getValidator(Class<T> classParameter) {
        DatasourceValidator<?> validator = validatorMap.get(classParameter);
        if (validator == null) {
            throw new IllegalArgumentException("No validator found for class: " + classParameter.getSimpleName());
        }
        return (DatasourceValidator<T>) validator;
    }
}

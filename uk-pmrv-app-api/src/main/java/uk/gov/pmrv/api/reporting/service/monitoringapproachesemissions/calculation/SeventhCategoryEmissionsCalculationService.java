package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SeventhCategoryEmissionsCalculationService extends SourceStreamEmissionsCalculationService {

    @Override
    protected BigDecimal getCalculationFactor(EmissionsCalculationParamsDTO calculationParams) {
        BigDecimal conversionFactor = calculationParams.getConversionFactor();

        if(conversionFactor == null) {
            throw new BusinessException(MetsErrorCode.AER_EMISSIONS_CALCULATION_PARAMETER_VALUE_MISSING);
        }

        return calculationParams.getEmissionFactor().multiply(conversionFactor);
    }

    @Override
    public SourceStreamCategory getSourceStreamCategory() {
        return SourceStreamCategory.CATEGORY_7;
    }

    @Override
    public List<CalculationParameterMeasurementUnits> getValidMeasurementUnitsCombinations() {
        List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = new ArrayList<>();
        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .build());
        
        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .build());

        return measurementUnitsCombinations;
    }
}

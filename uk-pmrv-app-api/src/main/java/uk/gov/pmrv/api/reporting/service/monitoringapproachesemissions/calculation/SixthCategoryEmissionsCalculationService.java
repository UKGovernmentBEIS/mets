package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class SixthCategoryEmissionsCalculationService extends SourceStreamEmissionsCalculationService {

    @Override
    protected BigDecimal getCalculationFactor(EmissionsCalculationParamsDTO calculationParams) {
        BigDecimal emissionFactor = calculationParams.getEmissionFactor();

        if(calculationParams.getEfMeasurementUnit() == EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ) {
            BigDecimal netCalorificValue = calculationParams.getNetCalorificValue();
            return emissionFactor.multiply(
                netCalorificValue.divide(BigDecimal.valueOf(1000), netCalorificValue.scale() + 3, RoundingMode.HALF_UP)
            );
        }

        return emissionFactor;
    }

    @Override
    public SourceStreamCategory getSourceStreamCategory() {
        return SourceStreamCategory.CATEGORY_6;
    }

    @Override
    public List<CalculationParameterMeasurementUnits> getValidMeasurementUnitsCombinations() {
        List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = new ArrayList<>();
        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build());

        return measurementUnitsCombinations;
    }
}

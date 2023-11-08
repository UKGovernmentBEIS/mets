package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class SecondCategoryEmissionsCalculationService extends SourceStreamEmissionsCalculationService {

    @Override
    protected BigDecimal getCalculationFactor(EmissionsCalculationParamsDTO calculationParams) {
        BigDecimal carbonContent = calculationParams.getCarbonContent();
        CarbonContentMeasurementUnit carbonContentMeasurementUnit = calculationParams.getCarbonContentMeasurementUnit();

        if(carbonContentMeasurementUnit == CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_NM3 ||
            carbonContentMeasurementUnit == CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_TONNE) {
            carbonContent = carbonContent.multiply(BigDecimal.valueOf(3.664));
        }

        return carbonContent;
    }

    @Override
    public SourceStreamCategory getSourceStreamCategory() {
        return SourceStreamCategory.CATEGORY_2;
    }

    @Override
    public List<CalculationParameterMeasurementUnits> getValidMeasurementUnitsCombinations() {
        List<CalculationParameterMeasurementUnits> measurementUnitsCombinations = new ArrayList<>();
        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .carbonContentMeasurementUnit(CarbonContentMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .carbonContentMeasurementUnit(CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_NM3)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .carbonContentMeasurementUnit(CarbonContentMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build());

        measurementUnitsCombinations.add(CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .carbonContentMeasurementUnit(CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_TONNE)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build());

        return measurementUnitsCombinations;
    }
}

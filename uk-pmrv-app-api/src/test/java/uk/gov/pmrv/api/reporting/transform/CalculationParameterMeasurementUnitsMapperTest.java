package uk.gov.pmrv.api.reporting.transform;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.CalculationParameterMeasurementUnits;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculationParameterMeasurementUnitsMapperTest {

    private final CalculationParameterMeasurementUnitsMapper mapper = Mappers.getMapper(CalculationParameterMeasurementUnitsMapper.class);

    @Test
    void toCalculationEmissionsMeasurementUnits() {
        EmissionsCalculationParamsDTO emissionCalculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(BigDecimal.valueOf(2345.60))
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .conversionFactor(BigDecimal.valueOf(99.99))
            .emissionFactor(BigDecimal.valueOf(34500.98))
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
            .netCalorificValue(BigDecimal.valueOf(23.4590823))
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        CalculationParameterMeasurementUnits expected = CalculationParameterMeasurementUnits.builder()
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        assertEquals(expected, mapper.toCalculationParameterMeasurementUnits(emissionCalculationParams));
    }
}
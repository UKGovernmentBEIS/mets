package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SeventhCategoryEmissionsCalculationServiceTest {

    private final SeventhCategoryEmissionsCalculationService service = new SeventhCategoryEmissionsCalculationService();

    @Test
    void getSourceStreamCategory() {
        assertEquals(SourceStreamCategory.CATEGORY_7, service.getSourceStreamCategory());
    }

    @Test
    void calculateEmissions() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        BigDecimal conversionFactor = BigDecimal.valueOf(2.45);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(true)
            .biomassPercentage(biomassPercentage)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .conversionFactor(conversionFactor)
            .build();

        BigDecimal biomass = biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP);

        BigDecimal calculationFactor = emissionFactor.multiply(conversionFactor).multiply(activityData);
        BigDecimal expectedFossilEmissions = roundTotal(calculationFactor.multiply(BigDecimal.ONE.subtract(biomass)));
        BigDecimal expectedBiomassEmissions = roundTotal(calculationFactor.multiply(biomass));

        EmissionsCalculationDTO emissionsCalculation = service.calculateEmissions(calculationParams);

        assertNotNull(emissionsCalculation);
        assertEquals(expectedFossilEmissions, emissionsCalculation.getReportableEmissions());
        assertEquals(expectedBiomassEmissions, emissionsCalculation.getSustainableBiomassEmissions());
    }

    @Test
    void calculateEmissions_exception_invalid_measurement_units() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        BigDecimal conversionFactor = BigDecimal.valueOf(2.45);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .containsBiomass(true)
            .biomassPercentage(biomassPercentage)
            .emissionFactor(emissionFactor)
            .conversionFactor(conversionFactor)
            .build();

        BusinessException be = assertThrows(BusinessException.class, () -> service.calculateEmissions(calculationParams));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION);
    }

    @Test
    void calculateEmissions_exception_when_no_conversion_factor() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(true)
            .biomassPercentage(biomassPercentage)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .build();

        BusinessException be = assertThrows(BusinessException.class, () -> service.calculateEmissions(calculationParams));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.AER_EMISSIONS_CALCULATION_PARAMETER_VALUE_MISSING);
    }

    BigDecimal roundTotal(BigDecimal value){
        return value.setScale(5, RoundingMode.HALF_UP);
    }
}
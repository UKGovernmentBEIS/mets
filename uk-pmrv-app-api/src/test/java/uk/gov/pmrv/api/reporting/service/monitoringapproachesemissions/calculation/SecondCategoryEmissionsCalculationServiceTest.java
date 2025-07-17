package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_TONNE;

class SecondCategoryEmissionsCalculationServiceTest {

    private final SecondCategoryEmissionsCalculationService service = new SecondCategoryEmissionsCalculationService();

    @Test
    void calculateEmissions_carbon_content_in_tonnes_of_carbon() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal carbonContent = BigDecimal.valueOf(188.034);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(true)
            .biomassPercentage(biomassPercentage)
            .carbonContent(carbonContent)
            .carbonContentMeasurementUnit(TONNES_OF_CARBON_PER_TONNE)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BigDecimal biomass = biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP);

        BigDecimal calculationFactor = carbonContent.multiply(BigDecimal.valueOf(3.664)).multiply(activityData);
        BigDecimal expectedFossilEmissions = roundTotal(calculationFactor.multiply(BigDecimal.ONE.subtract(biomass)));
        BigDecimal expectedBiomassEmissions = roundTotal(calculationFactor.multiply(biomass));

        EmissionsCalculationDTO emissionsCalculation = service.calculateEmissions(calculationParams);

        assertNotNull(emissionsCalculation);
        assertEquals(expectedFossilEmissions, emissionsCalculation.getReportableEmissions());
        assertEquals(expectedBiomassEmissions, emissionsCalculation.getSustainableBiomassEmissions());
    }

    @Test
    void calculateEmissions_carbon_content_in_tonnes_of_co2() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal carbonContent = BigDecimal.valueOf(188.034);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(true)
            .biomassPercentage(biomassPercentage)
            .carbonContent(carbonContent)
            .carbonContentMeasurementUnit(CarbonContentMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_NM3)
            .build();

        BigDecimal biomass = biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP);

        BigDecimal calculationFactor = carbonContent.multiply(activityData);
        BigDecimal expectedFossilEmissions = roundTotal(calculationFactor.multiply(BigDecimal.ONE.subtract(biomass)));
        BigDecimal expectedBiomassEmissions = roundTotal(calculationFactor.multiply(biomass));

        EmissionsCalculationDTO emissionsCalculation = service.calculateEmissions(calculationParams);

        assertNotNull(emissionsCalculation);
        assertEquals(expectedFossilEmissions, emissionsCalculation.getReportableEmissions());
        assertEquals(expectedBiomassEmissions, emissionsCalculation.getSustainableBiomassEmissions());
    }

    @Test
    void calculateEmissions_no_biomass() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal carbonContent = BigDecimal.valueOf(188.034);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(false)
            .carbonContent(carbonContent)
            .carbonContentMeasurementUnit(TONNES_OF_CARBON_PER_TONNE)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BigDecimal expectedFossilEmissions = roundTotal(carbonContent.multiply(BigDecimal.valueOf(3.664)).multiply(activityData));

        EmissionsCalculationDTO emissionsCalculation = service.calculateEmissions(calculationParams);

        assertNotNull(emissionsCalculation);
        assertNull(emissionsCalculation.getSustainableBiomassEmissions());
        assertEquals(expectedFossilEmissions, emissionsCalculation.getReportableEmissions());
    }

    @Test
    void getSourceStreamCategory() {
        assertEquals(SourceStreamCategory.CATEGORY_2, service.getSourceStreamCategory());
    }

    @ParameterizedTest
    @MethodSource("provideInputCalculationParams")
    void calculate(boolean containsBiomass, BigDecimal biomassPercentage, CarbonContentMeasurementUnit ccMeasurementUnit, BigDecimal extraCalcFactor) {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal carbonContent = BigDecimal.valueOf(188.034);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(containsBiomass)
            .biomassPercentage(biomassPercentage)
            .carbonContent(carbonContent)
            .carbonContentMeasurementUnit(ccMeasurementUnit)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BigDecimal biomass = containsBiomass
            ? biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal calculationFactor = carbonContent.multiply(activityData).multiply(extraCalcFactor);
        BigDecimal expectedFossilEmissions = roundTotal(calculationFactor.multiply(BigDecimal.ONE.subtract(biomass)));
        BigDecimal expectedBiomassEmissions = containsBiomass ? roundTotal(calculationFactor.multiply(biomass)) : null;

        EmissionsCalculationDTO emissionsCalculation = service.calculateEmissions(calculationParams);

        assertNotNull(emissionsCalculation);
        assertEquals(expectedFossilEmissions, emissionsCalculation.getReportableEmissions());
        assertEquals(expectedBiomassEmissions, emissionsCalculation.getSustainableBiomassEmissions());
    }

    @Test
    void calculateEmissions_exception_invalid_measurement_units() {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal carbonContent = BigDecimal.valueOf(188.034);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(false)
            .carbonContent(carbonContent)
            .carbonContentMeasurementUnit(TONNES_OF_CARBON_PER_TONNE)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BusinessException be = assertThrows(BusinessException.class, () -> service.calculateEmissions(calculationParams));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION);
    }

    private static Stream<Arguments> provideInputCalculationParams() {
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        BigDecimal extraCalcFactor = BigDecimal.valueOf(3.664);
        return Stream.of(
            //carbon content unit in tonnes of carbon
            Arguments.of(true, biomassPercentage, CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_TONNE, extraCalcFactor),
            //carbon content unit in tonnes of CO2
            Arguments.of(true, biomassPercentage, CarbonContentMeasurementUnit.TONNES_OF_CO2_PER_TONNE, BigDecimal.ONE),
            //no biomass
            Arguments.of(false, null, CarbonContentMeasurementUnit.TONNES_OF_CARBON_PER_TONNE, extraCalcFactor)
        );
    }


    BigDecimal roundTotal(BigDecimal value){
        return value.setScale(5, RoundingMode.HALF_UP);
    }
}
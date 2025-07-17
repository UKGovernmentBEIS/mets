package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SixthCategoryEmissionsCalculationServiceTest {

    private final SixthCategoryEmissionsCalculationService service = new SixthCategoryEmissionsCalculationService();

    @Test
    void getSourceStreamCategory() {
        assertEquals(SourceStreamCategory.CATEGORY_6, service.getSourceStreamCategory());
    }

    @ParameterizedTest
    @MethodSource("provideInputCalculationParams")
    void calculate(boolean containsBiomass, BigDecimal biomassPercentage, EmissionFactorMeasurementUnit efMeasurementUnit) {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(containsBiomass)
            .biomassPercentage(biomassPercentage)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(efMeasurementUnit)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BigDecimal ncv = netCalorificValue.divide(BigDecimal.valueOf(1000), netCalorificValue.scale() + 3, RoundingMode.HALF_UP);
        BigDecimal calculationFactor = efMeasurementUnit != EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ
            ? activityData.multiply(emissionFactor)
            : activityData.multiply(emissionFactor).multiply(ncv);
        BigDecimal biomass = containsBiomass
            ? biomassPercentage.divide(BigDecimal.valueOf(100), biomassPercentage.scale() + 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

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
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        BigDecimal netCalorificValue = BigDecimal.valueOf(0.03470698);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(false)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE)
            .netCalorificValue(netCalorificValue)
            .ncvMeasurementUnit(NCVMeasurementUnit.GJ_PER_TONNE)
            .build();

        BusinessException be = assertThrows(BusinessException.class, () -> service.calculateEmissions(calculationParams));

        assertThat(be.getErrorCode()).isEqualTo(MetsErrorCode.AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION);
    }

    private static Stream<Arguments> provideInputCalculationParams() {
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        return Stream.of(
            //emission factor unit in tonnes of co2 per tonne
            Arguments.of(true, biomassPercentage, EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TONNE),
            //emission factor unit in tonnes of co2 per terrajoule
            Arguments.of(true, biomassPercentage, EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ),
            //no biomass
            Arguments.of(false, null, EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_TJ)
        );
    }

    BigDecimal roundTotal(BigDecimal value){
        return value.setScale(5, RoundingMode.HALF_UP);
    }
}
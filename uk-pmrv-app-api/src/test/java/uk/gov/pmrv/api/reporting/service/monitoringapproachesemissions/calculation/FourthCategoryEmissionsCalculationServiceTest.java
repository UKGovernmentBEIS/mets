package uk.gov.pmrv.api.reporting.service.monitoringapproachesemissions.calculation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamCategory;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationDTO;
import uk.gov.pmrv.api.reporting.domain.dto.EmissionsCalculationParamsDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FourthCategoryEmissionsCalculationServiceTest {

    private final FourthCategoryEmissionsCalculationService service = new FourthCategoryEmissionsCalculationService();

    @Test
    void getSourceStreamCategory() {
        assertEquals(SourceStreamCategory.CATEGORY_4, service.getSourceStreamCategory());
    }

    @ParameterizedTest
    @MethodSource("provideInputCalculationParams")
    void calculate(boolean containsBiomass, BigDecimal biomassPercentage) {
        BigDecimal activityData = BigDecimal.valueOf(11.4897652311);
        BigDecimal emissionFactor = BigDecimal.valueOf(55.92);
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.NM3)
            .containsBiomass(containsBiomass)
            .biomassPercentage(biomassPercentage)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .build();

        BigDecimal calculationFactor = activityData.multiply(emissionFactor);
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
        EmissionsCalculationParamsDTO calculationParams = EmissionsCalculationParamsDTO.builder()
            .activityData(activityData)
            .activityDataMeasurementUnit(ActivityDataMeasurementUnit.TONNES)
            .containsBiomass(false)
            .emissionFactor(emissionFactor)
            .efMeasurementUnit(EmissionFactorMeasurementUnit.TONNES_OF_CO2_PER_NM3)
            .build();

        BusinessException be = assertThrows(BusinessException.class, () -> service.calculateEmissions(calculationParams));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.AER_EMISSIONS_CALCULATION_INVALID_MEASUREMENT_UNITS_COMBINATION);
    }

    private static Stream<Arguments> provideInputCalculationParams() {
        BigDecimal biomassPercentage = BigDecimal.valueOf(45.57);
        return Stream.of(
            Arguments.of(true, biomassPercentage),
            //no biomass
            Arguments.of(false, null)
        );
    }

    BigDecimal roundTotal(BigDecimal value){
        return value.setScale(5, RoundingMode.HALF_UP);
    }
}
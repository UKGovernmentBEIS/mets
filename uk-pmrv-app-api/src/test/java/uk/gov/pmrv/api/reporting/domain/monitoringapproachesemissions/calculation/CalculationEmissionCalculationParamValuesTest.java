package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import java.math.BigDecimal;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;

class CalculationEmissionCalculationParamValuesTest {

    @Test
    void shouldBeEqual() {
        CalculationEmissionCalculationParamValues emissionCalculationParamValues = createSampleValue(new BigDecimal("1.1"), null);
        CalculationEmissionCalculationParamValues emissionCalculationParamValues2 = createSampleValue(new BigDecimal("1.1"), null);
        Assert.assertTrue(emissionCalculationParamValues.equals(emissionCalculationParamValues2));
    }

    @Test
    void shouldNotBeEqual() {
        CalculationEmissionCalculationParamValues emissionCalculationParamValues = createSampleValue(new BigDecimal("1.1"), null);
        CalculationEmissionCalculationParamValues emissionCalculationParamValues2 = createSampleValue(new BigDecimal("1.2"), null);
        Assert.assertFalse(emissionCalculationParamValues.equals(emissionCalculationParamValues2));
    }

    private CalculationEmissionCalculationParamValues createSampleValue(BigDecimal netCalorificValue, ManuallyProvidedEmissions providedEmissions) {
        CalculationEmissionCalculationParamValues result = CalculationEmissionCalculationParamValues.builder()
            .netCalorificValue(netCalorificValue)
            .providedEmissions(providedEmissions)
            .build();
        return result;
    }

}
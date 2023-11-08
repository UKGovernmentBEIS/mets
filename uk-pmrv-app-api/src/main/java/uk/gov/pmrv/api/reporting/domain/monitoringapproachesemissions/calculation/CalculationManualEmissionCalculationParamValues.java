package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit;

import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#carbonContent == null) == (#carbonContentMeasurementUnit == null)}",
    message = "aer.calculationApproach.sourceStreamEmissions.emissionCalcParamValues.carbonContent.carbonContentMeasurementUnit")
public class CalculationManualEmissionCalculationParamValues extends CalculationEmissionCalculationParamValues {

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal carbonContent;

    private CarbonContentMeasurementUnit carbonContentMeasurementUnit;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal conversionFactor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationManualEmissionCalculationParamValues that = (CalculationManualEmissionCalculationParamValues) o;

        return ObjectUtils.compare(carbonContent, that.carbonContent) == 0
                && ObjectUtils.compare(conversionFactor, that.conversionFactor) == 0
                && ObjectUtils.compare(carbonContentMeasurementUnit, that.carbonContentMeasurementUnit) == 0
                && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), carbonContent, conversionFactor, conversionFactor);
    }
}

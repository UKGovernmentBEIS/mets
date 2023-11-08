package uk.gov.pmrv.api.reporting.domain.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.reporting.domain.ActivityDataMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.CarbonContentMeasurementUnit;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{#containsBiomass == (#biomassPercentage != null)}",
    message = "aer.calculationApproach.emissionsCalculation.biomassPercentage.biomassContains")
@SpELExpression(expression = "{(#carbonContent != null) == (#carbonContentMeasurementUnit != null)}",
    message = "aer.calculationApproach.emissionsCalculation.carbonContent.carbonContentMeasurementUnit")
public class EmissionsCalculationParamsDTO extends InventoryEmissionCalculationParamsDTO {

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal activityData;

    @NotNull
    private ActivityDataMeasurementUnit activityDataMeasurementUnit;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal carbonContent;

    private CarbonContentMeasurementUnit carbonContentMeasurementUnit;

    boolean containsBiomass;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal biomassPercentage;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal conversionFactor;

    @NotNull
    private SourceStreamType sourceStreamType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        EmissionsCalculationParamsDTO that = (EmissionsCalculationParamsDTO) o;

        return ObjectUtils.compare(activityData, that.activityData) == 0
                && ObjectUtils.compare(carbonContent, that.carbonContent) == 0
                && ObjectUtils.compare(biomassPercentage, that.biomassPercentage) == 0
                && ObjectUtils.compare(conversionFactor, that.conversionFactor) == 0
                && ObjectUtils.compare(activityDataMeasurementUnit, activityDataMeasurementUnit) == 0
                && ObjectUtils.compare(carbonContentMeasurementUnit, that.carbonContentMeasurementUnit) == 0
                && ObjectUtils.compare(sourceStreamType, that.sourceStreamType) == 0
                && ObjectUtils.compare(containsBiomass, that.containsBiomass) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), activityData, carbonContent, biomassPercentage, conversionFactor,
                activityDataMeasurementUnit, carbonContentMeasurementUnit, sourceStreamType, containsBiomass);
    }
}

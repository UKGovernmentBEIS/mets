package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#contains) == (#biomassPercentage != null)}",
        message = "aer.sourceStreamEmissions.biomassPercentage.contains")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#contains) == (#nonSustainableBiomassPercentage != null)}",
        message = "aer.sourceStreamEmissions.nonSustainableBiomassPercentage.contains")
@SpELExpression(expression = "{(#biomassPercentage == null) || (#nonSustainableBiomassPercentage == null) || " +
        "(#biomassPercentage).add(#nonSustainableBiomassPercentage).compareTo(new java.math.BigDecimal('100')) <= 0}",
        message = "aer.sourceStreamEmissions.totalBiomassPercentage.lessThanOrEqualTo100")
public class BiomassPercentages {

    private Boolean contains;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal biomassPercentage;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal nonSustainableBiomassPercentage;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BiomassPercentages that = (BiomassPercentages) o;

        return ObjectUtils.compare(biomassPercentage, that.biomassPercentage) == 0
                && ObjectUtils.compare(nonSustainableBiomassPercentage, that.nonSustainableBiomassPercentage) == 0
                && ObjectUtils.compare(contains, that.contains) == 0;

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), biomassPercentage, nonSustainableBiomassPercentage, contains);
    }
}

package uk.gov.pmrv.api.reporting.domain.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{#containsBiomass == (#biomassPercentage != null)}",
    message = "aer.calculationApproach.emissionsCalculation.biomassPercentage.biomassContains")
public class MeasurementEmissionsCalculationParamsDTO {

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal operationalHours;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal annualHourlyAverageFlueGasFlow;

    private boolean containsBiomass;

    @DecimalMin(value = "0")
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal biomassPercentage;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal annualHourlyAverageGHGConcentration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementEmissionsCalculationParamsDTO that = (MeasurementEmissionsCalculationParamsDTO) o;

        return ObjectUtils.compare(operationalHours, that.operationalHours) == 0
            && ObjectUtils.compare(annualHourlyAverageFlueGasFlow, that.annualHourlyAverageFlueGasFlow) == 0
            && ObjectUtils.compare(biomassPercentage, that.biomassPercentage) == 0
            && ObjectUtils.compare(containsBiomass, that.containsBiomass) == 0
            && ObjectUtils.compare(annualHourlyAverageGHGConcentration, that.annualHourlyAverageGHGConcentration) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(operationalHours, annualHourlyAverageFlueGasFlow,
            biomassPercentage, containsBiomass, annualHourlyAverageGHGConcentration);
    }
}

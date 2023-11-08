package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalculationInventoryEmissionCalculationParamValues extends CalculationEmissionCalculationParamValues {

    private BigDecimal calculationFactor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CalculationInventoryEmissionCalculationParamValues that = (CalculationInventoryEmissionCalculationParamValues) o;
        return ObjectUtils.compare(calculationFactor, that.calculationFactor) == 0
                && super.equals(that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), calculationFactor);
    }
}

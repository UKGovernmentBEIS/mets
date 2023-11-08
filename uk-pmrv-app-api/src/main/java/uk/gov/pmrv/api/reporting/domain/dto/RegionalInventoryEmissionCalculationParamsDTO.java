package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class RegionalInventoryEmissionCalculationParamsDTO extends InventoryEmissionCalculationParamsDTO {

    private BigDecimal calculationFactor;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegionalInventoryEmissionCalculationParamsDTO that)) return false;
        if (!super.equals(o)) return false;
        return ObjectUtils.compare(calculationFactor, that.calculationFactor) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), calculationFactor);
    }
}

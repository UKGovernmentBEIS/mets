package uk.gov.pmrv.api.reporting.domain.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SpELExpression(expression = "{#containsBiomass == (#totalNonSustainableBiomassEmissions != null)}",
    message = "aer.sourceStreamEmissions.fallbackBiomass.contains")
public class FallbackEmissionsCalculationParamsDTO {

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal totalFossilEmissions;

    private boolean containsBiomass;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalNonSustainableBiomassEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FallbackEmissionsCalculationParamsDTO that = (FallbackEmissionsCalculationParamsDTO) o;

        return ObjectUtils.compare(totalFossilEmissions, that.totalFossilEmissions) == 0
            && ObjectUtils.compare(totalNonSustainableBiomassEmissions, that.totalNonSustainableBiomassEmissions) == 0
            && ObjectUtils.compare(containsBiomass, that.containsBiomass) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalFossilEmissions, totalNonSustainableBiomassEmissions,
            containsBiomass);
    }
}

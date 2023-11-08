package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement.fallback;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#contains) == (#totalSustainableBiomassEmissions != " +
    "null)}",
    message = "aer.sourceStreamEmissions.fallbackBiomass.contains")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#contains) == (#totalNonSustainableBiomassEmissions " +
    "!= null)}",
    message = "aer.sourceStreamEmissions.fallbackBiomass.contains")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#contains) == (#totalEnergyContentFromBiomass " +
    "!= null)}",
    message = "aer.sourceStreamEmissions.fallbackBiomass.contains")
public class FallbackBiomass {

    private Boolean contains;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalSustainableBiomassEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalNonSustainableBiomassEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalEnergyContentFromBiomass;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FallbackBiomass that = (FallbackBiomass) o;

        return ObjectUtils.compare(totalSustainableBiomassEmissions, that.totalSustainableBiomassEmissions) == 0
            && ObjectUtils.compare(totalNonSustainableBiomassEmissions, that.totalNonSustainableBiomassEmissions) == 0
            && ObjectUtils.compare(totalEnergyContentFromBiomass, that.totalEnergyContentFromBiomass) == 0
            && ObjectUtils.compare(contains, that.contains) == 0;

    }

    @Override
    public int hashCode() {
        return Objects.hash(totalSustainableBiomassEmissions, totalNonSustainableBiomassEmissions,
            totalEnergyContentFromBiomass, contains);
    }
}

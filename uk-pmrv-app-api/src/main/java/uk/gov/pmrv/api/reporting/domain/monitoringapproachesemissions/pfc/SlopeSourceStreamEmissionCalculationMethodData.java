package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SlopeSourceStreamEmissionCalculationMethodData extends PfcSourceStreamEmissionCalculationMethodData {

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal anodeEffectsPerCellDay;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal averageDurationOfAnodeEffectsInMinutes;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal slopeCF4EmissionFactor;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        SlopeSourceStreamEmissionCalculationMethodData that = (SlopeSourceStreamEmissionCalculationMethodData) o;

        return ObjectUtils.compare(anodeEffectsPerCellDay, that.anodeEffectsPerCellDay) == 0
            && ObjectUtils.compare(averageDurationOfAnodeEffectsInMinutes, that.averageDurationOfAnodeEffectsInMinutes) == 0
            && ObjectUtils.compare(slopeCF4EmissionFactor, that.slopeCF4EmissionFactor) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), anodeEffectsPerCellDay, averageDurationOfAnodeEffectsInMinutes, slopeCF4EmissionFactor);
    }
}

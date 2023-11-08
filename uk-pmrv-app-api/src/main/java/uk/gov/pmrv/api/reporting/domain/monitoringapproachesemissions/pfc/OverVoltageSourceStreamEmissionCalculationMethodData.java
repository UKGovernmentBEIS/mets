package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class OverVoltageSourceStreamEmissionCalculationMethodData extends PfcSourceStreamEmissionCalculationMethodData {

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal anodeEffectsOverVoltagePerCell;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "100.00000")
    @Digits(integer = 3, fraction = 5)
    private BigDecimal aluminiumAverageCurrentEfficiencyProduction;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal overVoltageCoefficient;

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

        OverVoltageSourceStreamEmissionCalculationMethodData that = (OverVoltageSourceStreamEmissionCalculationMethodData) o;

        return ObjectUtils.compare(anodeEffectsOverVoltagePerCell, that.anodeEffectsOverVoltagePerCell) == 0
            && ObjectUtils.compare(aluminiumAverageCurrentEfficiencyProduction, that.aluminiumAverageCurrentEfficiencyProduction) == 0
            && ObjectUtils.compare(overVoltageCoefficient, that.overVoltageCoefficient) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), anodeEffectsOverVoltagePerCell, aluminiumAverageCurrentEfficiencyProduction, overVoltageCoefficient);
    }
}

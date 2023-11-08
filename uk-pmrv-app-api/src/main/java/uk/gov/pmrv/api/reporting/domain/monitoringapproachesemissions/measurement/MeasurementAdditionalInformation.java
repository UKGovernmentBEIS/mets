package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.measurement;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeasurementAdditionalInformation {

    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE)
    private BigDecimal fossilEmissionsTotalEnergyContent;

    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE)
    private BigDecimal biomassEmissionsTotalEnergyContent;

    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE)
    private BigDecimal fossilEmissionsCorroboratingCalculation;

    @Digits(integer = Integer.MAX_VALUE, fraction = Integer.MAX_VALUE)
    private BigDecimal biomassEmissionsCorroboratingCalculation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeasurementAdditionalInformation that = (MeasurementAdditionalInformation) o;

        return ObjectUtils.compare(fossilEmissionsTotalEnergyContent, that.fossilEmissionsTotalEnergyContent) == 0
            && ObjectUtils.compare(biomassEmissionsTotalEnergyContent, that.biomassEmissionsTotalEnergyContent) == 0
            && ObjectUtils.compare(fossilEmissionsCorroboratingCalculation,
            that.fossilEmissionsCorroboratingCalculation) == 0
            && ObjectUtils.compare(biomassEmissionsCorroboratingCalculation,
            that.biomassEmissionsCorroboratingCalculation) == 0;

    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fossilEmissionsTotalEnergyContent, biomassEmissionsTotalEnergyContent,
            fossilEmissionsCorroboratingCalculation, biomassEmissionsCorroboratingCalculation);
    }
}

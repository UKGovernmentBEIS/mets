package uk.gov.pmrv.api.reporting.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MeasurementEmissionsCalculationDTO extends EmissionsCalculationDTO {

    private BigDecimal globalWarmingPotential;
    private BigDecimal annualGasFlow;
    private BigDecimal annualFossilAmountOfGreenhouseGas;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MeasurementEmissionsCalculationDTO that = (MeasurementEmissionsCalculationDTO) o;

        return ObjectUtils.compare(globalWarmingPotential, that.globalWarmingPotential) == 0
            && ObjectUtils.compare(annualGasFlow, that.annualGasFlow) == 0
            && ObjectUtils.compare(annualFossilAmountOfGreenhouseGas, that.annualFossilAmountOfGreenhouseGas) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), globalWarmingPotential, annualGasFlow, annualFossilAmountOfGreenhouseGas);
    }
}

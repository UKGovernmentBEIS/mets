package uk.gov.pmrv.api.reporting.domain.dto;

import jakarta.validation.constraints.Digits;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#emissionFactor != null) == (#efMeasurementUnit != null)}",
    message = "aer.calculationApproach.emissionsCalculation.emissionFactor.efMeasurementUnit")
@SpELExpression(expression = "{(#netCalorificValue != null) == (#ncvMeasurementUnit != null)}",
    message = "aer.calculationApproach.emissionsCalculation.netCalorificValue.ncvMeasurementUnit")
public class InventoryEmissionCalculationParamsDTO {

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal emissionFactor;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal netCalorificValue;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal oxidationFactor;

    private NCVMeasurementUnit ncvMeasurementUnit;
    private EmissionFactorMeasurementUnit efMeasurementUnit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryEmissionCalculationParamsDTO that = (InventoryEmissionCalculationParamsDTO) o;

        return ObjectUtils.compare(emissionFactor, that.emissionFactor) == 0
            && ObjectUtils.compare(netCalorificValue, that.netCalorificValue) == 0
            && ObjectUtils.compare(oxidationFactor, that.oxidationFactor) == 0
            && ObjectUtils.compare(ncvMeasurementUnit, that.ncvMeasurementUnit) == 0
            && ObjectUtils.compare(efMeasurementUnit, that.efMeasurementUnit) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(emissionFactor, netCalorificValue, oxidationFactor, ncvMeasurementUnit,
            efMeasurementUnit);
    }
}

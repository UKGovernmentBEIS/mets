package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.ObjectUtils;
import uk.gov.netz.api.common.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.EmissionFactorMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.NCVMeasurementUnit;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ManuallyProvidedEmissions;

import java.math.BigDecimal;
import java.util.Objects;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#netCalorificValue == null) == (#ncvMeasurementUnit == null)}",
        message = "aer.calculationApproach.sourceStreamEmissions.emissionCalcParamValues.netCalorificValue.ncvMeasurementUnit")
@SpELExpression(expression = "{(#emissionFactor == null) == (#efMeasurementUnit == null)}",
        message = "aer.calculationApproach.sourceStreamEmissions.emissionCalcParamValues.emissionFactor.efMeasurementUnit")
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#calculationCorrect) == (#providedEmissions != null)}",
        message = "aer.calculationApproach.sourceStreamEmissions.emissionCalcParamValues.providedEmissions.calculationCorrect")
public class CalculationEmissionCalculationParamValues {

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal netCalorificValue;

    private NCVMeasurementUnit ncvMeasurementUnit;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal emissionFactor;

    private EmissionFactorMeasurementUnit efMeasurementUnit;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal oxidationFactor;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    @NotNull
    private BigDecimal totalReportableEmissions;

    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalSustainableBiomassEmissions;

    @NotNull
    private Boolean calculationCorrect;

    @Valid
    private ManuallyProvidedEmissions providedEmissions;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalculationEmissionCalculationParamValues that = (CalculationEmissionCalculationParamValues) o;

        return ObjectUtils.compare(netCalorificValue, that.netCalorificValue) == 0
                && ObjectUtils.compare(emissionFactor, that.emissionFactor) == 0
                && ObjectUtils.compare(oxidationFactor, that.oxidationFactor) == 0
                && ObjectUtils.compare(totalReportableEmissions, that.totalReportableEmissions) == 0
                && ObjectUtils.compare(totalSustainableBiomassEmissions, that.totalSustainableBiomassEmissions) == 0
                && ObjectUtils.compare(ncvMeasurementUnit, that.ncvMeasurementUnit) == 0
                && ObjectUtils.compare(efMeasurementUnit, that.efMeasurementUnit) == 0
                && ObjectUtils.compare(calculationCorrect, that.calculationCorrect) == 0
                && Objects.equals(providedEmissions, that.providedEmissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), netCalorificValue, emissionFactor, oxidationFactor, totalReportableEmissions,
                totalSustainableBiomassEmissions, ncvMeasurementUnit, efMeasurementUnit, calculationCorrect, providedEmissions);
    }
}

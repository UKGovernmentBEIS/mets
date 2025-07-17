package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#materialImportedOrExported) == (#materialImportedQuantity != null)}",
    message = "aer.calculationApproach.sourceStreamEmissions.activityData.materialImported.quantity")
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#materialImportedOrExported) == (#materialExportedQuantity != null)}",
    message = "aer.calculationApproach.sourceStreamEmissions.activityData.materialExported.quantity")
public class CalculationActivityDataAggregationMeteringCalcMethod extends CalculationActivityDataCalculationMethod {

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    @NotNull
    private BigDecimal materialOpeningQuantity;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    @NotNull
    private BigDecimal materialClosingQuantity;

    private Boolean materialImportedOrExported;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal materialImportedQuantity;

    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal materialExportedQuantity;


}

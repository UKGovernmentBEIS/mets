package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
@SpELExpression(expression = "{(#totalEmissionsType eq 'FULL_SCOPE_FLIGHTS') == (#fullScopeTotalEmissions != null)}", message = "aviationAer.monitoringApproach.fullScopeTotalEmissions")
@SpELExpression(expression = "{(#totalEmissionsType eq 'AVIATION_ACTIVITY') == (#aviationActivityTotalEmissions != null)}", message = "aviationAer.monitoringApproach.aviationActivityTotalEmissions")
public class AviationAerSupportFacilityMonitoringApproach extends AviationAerEmissionsMonitoringApproach {

    @NotNull
    private TotalEmissionsType totalEmissionsType;

    @Digits(integer = 5, fraction = 3)
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "25000.000")
    private BigDecimal fullScopeTotalEmissions;

    @Digits(integer = 4, fraction = 3)
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "3000.000")
    private BigDecimal aviationActivityTotalEmissions;
}

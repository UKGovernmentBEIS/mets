package uk.gov.pmrv.api.aviationreporting.ukets.domain.emissionsmonitoringapproach;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
@SpELExpression(expression = "{(#numOfFlightsJanApr lt 243 && #numOfFlightsMayAug lt 243 && #numOfFlightsSepDec lt 243 ) || (#totalEmissions.compareTo(25000) < 0)}", 
message = "aviationAer.monitoringApproach.numberOfFLightsOrTotalEmissions")
public class AviationAerSmallEmittersMonitoringApproach extends AviationAerEmissionsMonitoringApproach {

    @NotNull
    @Min(0)
    @Max(9999999999L)
    private Long numOfFlightsJanApr;

    @NotNull
    @Min(0)
    @Max(9999999999L)
    private Long numOfFlightsMayAug;

    @NotNull
    @Min(0)
    @Max(9999999999L)
    private Long numOfFlightsSepDec;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @Digits(integer = Integer.MAX_VALUE, fraction = 3)
    private BigDecimal totalEmissions;
}

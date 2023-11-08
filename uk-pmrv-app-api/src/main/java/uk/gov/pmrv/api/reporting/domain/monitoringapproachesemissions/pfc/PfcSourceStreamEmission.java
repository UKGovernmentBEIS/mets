package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.pfc;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{T(java.lang.Boolean).FALSE.equals(#calculationCorrect) == (#providedEmissions != null)}",
    message = "aer.pfcApproach.sourceStreamEmissions.providedEmissions.calculationCorrect")
@SpELExpression(expression = "{#massBalanceApproachUsed || (#massBalanceApproachUsed == false && " +
    "(#parameterMonitoringTier.activityDataTier eq 'TIER_1' || #parameterMonitoringTier.activityDataTier eq 'TIER_2')" +
    ")}",
    message = "aer.pfcApproach.sourceStreamCategoryAppliedTiers.activityData.invalidTier")
public class PfcSourceStreamEmission {

    /**
     * Should be populated only for pfcSourceStreamEmissions that come from the permit object and are created during
     * the initialization of the aer request
     * payload.
     */
    private String id;

    @NotBlank
    private String sourceStream;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @Valid
    @NotNull
    private CalculationPfcParameterMonitoringTier parameterMonitoringTier;

    @NotNull
    @Digits(integer = 12, fraction = Integer.MAX_VALUE)
    private BigDecimal totalPrimaryAluminium;

    @Valid
    @NotNull
    private DurationRange durationRange;

    @Valid
    private ParameterMonitoringTierDiffReason parameterMonitoringTierDiffReason;

    @Valid
    @NotNull
    private PfcSourceStreamEmissionCalculationMethodData pfcSourceStreamEmissionCalculationMethodData;

    @NotNull
    private Boolean calculationCorrect;

    private boolean massBalanceApproachUsed;

    @Valid
    private PfcManuallyProvidedEmissions providedEmissions;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal amountOfCF4;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalCF4Emissions;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal amountOfC2F6;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal totalC2F6Emissions;

    @NotNull
    @Digits(integer = Integer.MAX_VALUE, fraction = 5)
    private BigDecimal reportableEmissions;
}

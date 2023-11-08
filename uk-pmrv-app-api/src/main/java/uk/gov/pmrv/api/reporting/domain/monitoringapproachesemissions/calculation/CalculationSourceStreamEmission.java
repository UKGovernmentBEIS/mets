package uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.calculation;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.TransferCO2;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.BiomassPercentages;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.DurationRange;
import uk.gov.pmrv.api.reporting.domain.monitoringapproachesemissions.common.ParameterMonitoringTierDiffReason;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculationSourceStreamEmission {

    /**
     * Should be populated only for calculationSourceStreamEmissions that come from the permit object and are
     * created during the initialization of the aer request payload.
     */
    private String id;

    @NotBlank
    private String sourceStream;

    @NotEmpty
    @Builder.Default
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<String> emissionSources = new LinkedHashSet<>();

    @Valid
    @NotEmpty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Builder.Default
    private List<CalculationParameterMonitoringTier> parameterMonitoringTiers = new ArrayList<>();

    @Valid
    @NotNull
    private BiomassPercentages biomassPercentages;

    @Valid
    @NotNull
    private DurationRange durationRange;

    @Valid
    private ParameterMonitoringTierDiffReason parameterMonitoringTierDiffReason;

    @Valid
    @NotNull
    private CalculationParameterCalculationMethod parameterCalculationMethod;

    @Valid
    private TransferCO2 transfer;
}

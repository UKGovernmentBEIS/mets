package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(#samplingFrequency == 'OTHER') == (#samplingFrequencyOtherDetails != null)}", 
    message = "permit.calculationApproach.tier.common.analysisMethod.samplingFrequencyOtherDetails")
@SpELExpression(expression = "{!#frequencyMeetsMinRequirements == (#reducedSamplingFrequencyJustification != null)}",
    message = "permit.calculationApproach.tier.common.frequencyMeetsMinRequirements_false.reducedSamplingFrequencyJustification")
public class CalculationAnalysisMethod {

    @Size(max=1000)
    @NotBlank
    private String analysis;

    @Size(max = 250)
    private String subParameter;

    @NotNull
    private CalculationSamplingFrequency samplingFrequency;
    
    @Size(max = 500)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String samplingFrequencyOtherDetails;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean frequencyMeetsMinRequirements;

    @Valid
    @JsonUnwrapped
    private CalculationLaboratory laboratory;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();

    @Valid
    private ReducedSamplingFrequencyJustification reducedSamplingFrequencyJustification;

}

package uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculationco2;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@SpELExpression(expression = "{(T(java.lang.Boolean).TRUE.equals(#isCostUnreasonable) || T(java.lang.Boolean).TRUE.equals(#isOneThirdRuleAndSampling))}",
    message = "permit.calculationApproach.tier.reducedSamplingFrequencyJustification.at_least_one_mandatory")
public class ReducedSamplingFrequencyJustification {
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isCostUnreasonable;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isOneThirdRuleAndSampling;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}

package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.netz.api.common.validation.SpELExpression;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "{T(java.lang.Boolean).TRUE.equals(#improvementRequired) == (#improvementDeadline != null)}", message = "regulatorImprovementResponse.improvementRequired")
public class RegulatorAirImprovementResponse {

    @NotNull
    private Boolean improvementRequired;
    
    @Future
    private LocalDate improvementDeadline;

    @NotNull
    @Size(max = 10000)
    private String officialResponse;

    @NotNull
    @Size(max = 10000)
    private String comments;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}

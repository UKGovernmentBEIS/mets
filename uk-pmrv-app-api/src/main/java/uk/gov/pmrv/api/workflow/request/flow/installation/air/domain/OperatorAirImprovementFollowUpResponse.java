package uk.gov.pmrv.api.workflow.request.flow.installation.air.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.dto.validation.SpELExpression;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SpELExpression(expression = "T(java.lang.Boolean).TRUE.equals(#improvementCompleted) == (#dateCompleted != null)", message = "operatorImprovementFollowUpResponse.dateCompleted")
@SpELExpression(expression = "T(java.lang.Boolean).FALSE.equals(#improvementCompleted) == (#reason != null)", message = "operatorImprovementFollowUpResponse.reason")
public class OperatorAirImprovementFollowUpResponse {

    @NotNull
    private Boolean improvementCompleted;

    @PastOrPresent
    private LocalDate dateCompleted;

    @Size(max = 10000)
    private String reason;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> files = new HashSet<>();
}

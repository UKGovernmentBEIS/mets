package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload extends RequestTaskActionPayload {
    
    @NotNull
    @Valid
    private PermitSurrenderReviewDecision reviewDecision;

    private Boolean reviewDeterminationCompleted;
    
}

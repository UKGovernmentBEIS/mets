package uk.gov.pmrv.api.workflow.request.flow.installation.permitsurrender.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermitSurrenderReviewDecision {

    @NotNull
    private PermitSurrenderReviewDecisionType type;

    @Valid
    private ReviewDecisionDetails details;
    
}

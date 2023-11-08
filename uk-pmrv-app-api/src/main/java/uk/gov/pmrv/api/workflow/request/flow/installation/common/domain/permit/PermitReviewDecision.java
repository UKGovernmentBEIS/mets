package uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitReviewDecision {

    @NotNull
    private ReviewDecisionType type;
}

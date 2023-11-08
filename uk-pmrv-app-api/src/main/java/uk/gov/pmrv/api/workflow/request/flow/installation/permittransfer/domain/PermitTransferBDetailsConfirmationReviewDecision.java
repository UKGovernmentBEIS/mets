package uk.gov.pmrv.api.workflow.request.flow.installation.permittransfer.domain;

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
public class PermitTransferBDetailsConfirmationReviewDecision {

    @NotNull
    private PermitTransferBDetailsConfirmationReviewDecisionType type;

    @Valid
    private ReviewDecisionDetails details;
}

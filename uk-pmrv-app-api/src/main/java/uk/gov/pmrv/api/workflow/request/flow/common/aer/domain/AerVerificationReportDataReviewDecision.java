package uk.gov.pmrv.api.workflow.request.flow.common.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionDetails;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AerVerificationReportDataReviewDecision extends AerReviewDecision {

    @NotNull
    private AerVerificationReportDataReviewDecisionType type;

    @Valid
    private ReviewDecisionDetails details;
}

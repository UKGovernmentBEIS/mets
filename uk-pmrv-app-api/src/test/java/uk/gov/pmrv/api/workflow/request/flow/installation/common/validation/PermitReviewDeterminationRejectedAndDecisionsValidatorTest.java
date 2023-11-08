package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceReviewDecision;

class PermitReviewDeterminationRejectedAndDecisionsValidatorTest {

	@Test
	void isValid_valid() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = Map.of(
				PermitReviewGroup.CALCULATION_CO2, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.REJECTED).build(),
				PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
				);

		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();

		assertThat(new PermitReviewDeterminationRejectedAndDecisionsValidator<PermitIssuanceReviewDecision>().isValid(taskPayload)).isTrue();
	}

	@Test
	void isValid_invalid() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = Map.of(
				PermitReviewGroup.CALCULATION_CO2, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build(),
				PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().type(ReviewDecisionType.ACCEPTED).build()
				);

		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();

		assertThat(new PermitReviewDeterminationRejectedAndDecisionsValidator<PermitIssuanceReviewDecision>().isValid(taskPayload)).isFalse();
	}
}

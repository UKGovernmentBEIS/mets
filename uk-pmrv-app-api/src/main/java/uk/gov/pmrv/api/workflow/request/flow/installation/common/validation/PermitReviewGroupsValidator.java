package uk.gov.pmrv.api.workflow.request.flow.installation.common.validation;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitPayloadGroupDecidable;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.utils.PermitReviewUtils;

import java.util.Map;
import java.util.Set;

@Service
public class PermitReviewGroupsValidator<T extends PermitReviewDecision> {

    public boolean containsDecisionForAllPermitGroups(final PermitPayloadGroupDecidable<T> taskPayload) {
		final Map<PermitReviewGroup, T> permitReviewGroupDecisions = taskPayload
				.getReviewGroupDecisions();

        final Set<PermitReviewGroup> permitReviewGroups = PermitReviewUtils.getPermitReviewGroups(taskPayload.getPermit());

        return CollectionUtils.isEqualCollection(permitReviewGroupDecisions.keySet(), permitReviewGroups);
    }

    public boolean containsAmendNeededGroups(final PermitPayloadGroupDecidable<T> taskPayload) {
    	return taskPayload
				.getReviewGroupDecisions().values().stream()
                .anyMatch(dec -> dec.getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED));

    }
}

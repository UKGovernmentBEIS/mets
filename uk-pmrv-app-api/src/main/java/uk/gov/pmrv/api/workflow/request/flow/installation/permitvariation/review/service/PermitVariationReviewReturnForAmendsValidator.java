package uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitvariation.review.domain.PermitVariationApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitVariationReviewReturnForAmendsValidator {

    public void validate(final PermitVariationApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> ReviewDecisionType.OPERATOR_AMENDS_NEEDED == reviewDecision.getType()) || 
            (taskPayload.getPermitVariationDetailsReviewDecision() != null && taskPayload.getPermitVariationDetailsReviewDecision().getType() == ReviewDecisionType.OPERATOR_AMENDS_NEEDED);
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_VARIATION_REVIEW);
        }
    }
}

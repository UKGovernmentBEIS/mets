package uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class PermitReviewReturnForAmendsValidatorService {

    public void validate(final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED));
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_PERMIT_REVIEW);
        }
    }
}

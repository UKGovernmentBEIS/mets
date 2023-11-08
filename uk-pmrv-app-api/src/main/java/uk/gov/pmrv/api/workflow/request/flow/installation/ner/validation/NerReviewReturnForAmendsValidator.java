package uk.gov.pmrv.api.workflow.request.flow.installation.ner.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.review.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.installation.ner.domain.NerApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class NerReviewReturnForAmendsValidator {

    public void validate(final NerApplicationReviewRequestTaskPayload taskPayload) {

        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(ReviewDecisionType.OPERATOR_AMENDS_NEEDED));
        if (!amendExists) {
            throw new BusinessException(ErrorCode.INVALID_NER);
        }
    }
}

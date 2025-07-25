package uk.gov.pmrv.api.workflow.request.flow.installation.aer.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerDataReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.aer.domain.AerReviewDataType;
import uk.gov.pmrv.api.workflow.request.flow.installation.aer.domain.AerApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class AerReviewReturnForAmendsValidatorService {

    public void validate(final AerApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .filter(reviewDecision -> reviewDecision.getReviewDataType().equals(AerReviewDataType.AER_DATA))
            .map(AerDataReviewDecision.class::cast)
            .anyMatch(reviewDecision -> reviewDecision.getType() == AerDataReviewDecisionType.OPERATOR_AMENDS_NEEDED);

        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_AER_REVIEW);
        }
    }
}

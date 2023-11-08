package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceUkEtsReviewReturnForAmendsValidatorService {

	public void validate(final EmpIssuanceUkEtsApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED));
        if (!amendExists) {
            throw new BusinessException(ErrorCode.INVALID_EMP_REVIEW);
        }
    }
}

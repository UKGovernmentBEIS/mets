package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationUkEtsReviewReturnForAmendsValidatorService {

	public void validate(final EmpVariationUkEtsApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)) 
            || (taskPayload.getEmpVariationDetailsReviewDecision() != null 
            	&& taskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED);
        if (!amendExists) {
            throw new BusinessException(ErrorCode.INVALID_EMP_VARIATION_REVIEW);
        }
    }
}

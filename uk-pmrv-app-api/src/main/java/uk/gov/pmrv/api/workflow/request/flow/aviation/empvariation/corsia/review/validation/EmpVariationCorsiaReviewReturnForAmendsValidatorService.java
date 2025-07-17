package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpVariationCorsiaReviewReturnForAmendsValidatorService {

	public void validate(final EmpVariationCorsiaApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED)) 
            || (taskPayload.getEmpVariationDetailsReviewDecision() != null 
            	&& taskPayload.getEmpVariationDetailsReviewDecision().getType() == EmpVariationReviewDecisionType.OPERATOR_AMENDS_NEEDED);
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_EMP_VARIATION_REVIEW);
        }
    }
}

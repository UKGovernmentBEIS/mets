package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.MetsErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

@Service
@RequiredArgsConstructor
public class EmpIssuanceCorsiaReviewReturnForAmendsValidatorService {

	public void validate(final EmpIssuanceCorsiaApplicationReviewRequestTaskPayload taskPayload) {

        // Validate if operator amends needed exist
        boolean amendExists = taskPayload.getReviewGroupDecisions().values().stream()
            .anyMatch(reviewDecision -> reviewDecision.getType().equals(EmpReviewDecisionType.OPERATOR_AMENDS_NEEDED));
        if (!amendExists) {
            throw new BusinessException(MetsErrorCode.INVALID_EMP_REVIEW);
        }
    }
}

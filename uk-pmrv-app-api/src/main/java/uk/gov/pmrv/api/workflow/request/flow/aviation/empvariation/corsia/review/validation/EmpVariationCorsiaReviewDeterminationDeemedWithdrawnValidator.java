package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

@Service
public class EmpVariationCorsiaReviewDeterminationDeemedWithdrawnValidator 
	implements EmpVariationCorsiaReviewDeterminationTypeValidator {

	@Override
    public boolean isValid(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return true;
    }

    @Override
    public EmpVariationDeterminationType getType() {
        return EmpVariationDeterminationType.DEEMED_WITHDRAWN;
    }
}

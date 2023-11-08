package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

@Service
public class EmpVariationUkEtsReviewDeterminationDeemedWithdrawnValidator 
	implements EmpVariationUkEtsReviewDeterminationTypeValidator {

	@Override
    public boolean isValid(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return true;
    }

    @Override
    public EmpVariationDeterminationType getType() {
        return EmpVariationDeterminationType.DEEMED_WITHDRAWN;
    }
}

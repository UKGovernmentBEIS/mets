package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.validation;

import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.ukets.review.domain.EmpVariationUkEtsApplicationReviewRequestTaskPayload;

public interface EmpVariationUkEtsReviewDeterminationTypeValidator {
	
	boolean isValid(EmpVariationUkEtsApplicationReviewRequestTaskPayload requestTaskPayload);

    @NotNull
    EmpVariationDeterminationType getType();
}

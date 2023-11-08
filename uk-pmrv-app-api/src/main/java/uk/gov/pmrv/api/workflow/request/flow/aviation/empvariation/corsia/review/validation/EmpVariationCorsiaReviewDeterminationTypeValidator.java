package uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.validation;

import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.common.domain.EmpVariationDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empvariation.corsia.review.domain.EmpVariationCorsiaApplicationReviewRequestTaskPayload;

public interface EmpVariationCorsiaReviewDeterminationTypeValidator {
	
	boolean isValid(EmpVariationCorsiaApplicationReviewRequestTaskPayload requestTaskPayload);

    @NotNull
    EmpVariationDeterminationType getType();
}

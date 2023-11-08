package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

public interface EmpIssuanceUkEtsReviewDeterminationTypeValidator {

    boolean isValid(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload);

    @NotNull
    EmpIssuanceDeterminationType getType();
}

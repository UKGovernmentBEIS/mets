package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import jakarta.validation.constraints.NotNull;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

public interface EmpIssuanceCorsiaReviewDeterminationTypeValidator {

    boolean isValid(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload);

    @NotNull
    EmpIssuanceDeterminationType getType();
}

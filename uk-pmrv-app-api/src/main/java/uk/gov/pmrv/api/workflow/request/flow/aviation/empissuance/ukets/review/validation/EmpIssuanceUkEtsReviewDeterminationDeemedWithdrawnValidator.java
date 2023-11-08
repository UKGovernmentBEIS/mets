package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

@Service
public class EmpIssuanceUkEtsReviewDeterminationDeemedWithdrawnValidator
    implements EmpIssuanceUkEtsReviewDeterminationTypeValidator {

    @Override
    public boolean isValid(EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload) {
        return true;
    }

    @Override
    public EmpIssuanceDeterminationType getType() {
        return EmpIssuanceDeterminationType.DEEMED_WITHDRAWN;
    }
}

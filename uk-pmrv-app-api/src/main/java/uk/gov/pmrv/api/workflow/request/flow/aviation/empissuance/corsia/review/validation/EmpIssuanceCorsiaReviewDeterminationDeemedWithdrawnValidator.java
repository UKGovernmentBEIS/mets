package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

@Service
public class EmpIssuanceCorsiaReviewDeterminationDeemedWithdrawnValidator
    implements EmpIssuanceCorsiaReviewDeterminationTypeValidator {

    @Override
    public boolean isValid(EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload) {
        return true;
    }

    @Override
    public EmpIssuanceDeterminationType getType() {
        return EmpIssuanceDeterminationType.DEEMED_WITHDRAWN;
    }
}

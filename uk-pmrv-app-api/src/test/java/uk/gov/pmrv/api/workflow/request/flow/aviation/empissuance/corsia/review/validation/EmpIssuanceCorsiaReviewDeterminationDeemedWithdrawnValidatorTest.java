package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.corsia.review.domain.EmpIssuanceCorsiaApplicationReviewRequestTaskPayload;

class EmpIssuanceCorsiaReviewDeterminationDeemedWithdrawnValidatorTest {

    private final EmpIssuanceCorsiaReviewDeterminationDeemedWithdrawnValidator validator = new EmpIssuanceCorsiaReviewDeterminationDeemedWithdrawnValidator();

    @Test
    void isValid() {
        EmpIssuanceCorsiaApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceCorsiaApplicationReviewRequestTaskPayload.builder().build();
        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void getType() {
        assertEquals(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN, validator.getType());
    }
}
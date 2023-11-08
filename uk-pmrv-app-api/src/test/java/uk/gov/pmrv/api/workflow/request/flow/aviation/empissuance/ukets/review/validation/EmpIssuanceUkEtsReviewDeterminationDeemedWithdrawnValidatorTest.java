package uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.validation;

import org.junit.jupiter.api.Test;

import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.common.domain.EmpIssuanceDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.aviation.empissuance.ukets.review.domain.EmpIssuanceUkEtsApplicationReviewRequestTaskPayload;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmpIssuanceUkEtsReviewDeterminationDeemedWithdrawnValidatorTest {

    private final EmpIssuanceUkEtsReviewDeterminationDeemedWithdrawnValidator validator = new EmpIssuanceUkEtsReviewDeterminationDeemedWithdrawnValidator();

    @Test
    void isValid() {
        EmpIssuanceUkEtsApplicationReviewRequestTaskPayload requestTaskPayload = EmpIssuanceUkEtsApplicationReviewRequestTaskPayload.builder().build();
        assertTrue(validator.isValid(requestTaskPayload));
    }

    @Test
    void getType() {
        assertEquals(EmpIssuanceDeterminationType.DEEMED_WITHDRAWN, validator.getType());
    }
}
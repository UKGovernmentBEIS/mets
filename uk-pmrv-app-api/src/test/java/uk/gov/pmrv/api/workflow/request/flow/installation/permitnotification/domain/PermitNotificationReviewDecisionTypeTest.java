package uk.gov.pmrv.api.workflow.request.flow.installation.permitnotification.domain;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.installation.common.domain.permit.DeterminationType;

import static org.junit.jupiter.api.Assertions.*;

class PermitNotificationReviewDecisionTypeTest {

    @Test
    void test_toDeterminationType() {

        assertEquals(DeterminationType.COMPLETED,
                PermitNotificationReviewDecisionType.CESSATION_TREATED_AS_PERMANENT.toDeterminationType());

        assertEquals(DeterminationType.COMPLETED,
                PermitNotificationReviewDecisionType.PERMANENT_CESSATION.toDeterminationType());

        assertEquals(DeterminationType.COMPLETED,
                PermitNotificationReviewDecisionType.TEMPORARY_CESSATION.toDeterminationType());

        assertEquals(DeterminationType.COMPLETED,
                PermitNotificationReviewDecisionType.NOT_CESSATION.toDeterminationType());

        assertEquals(DeterminationType.GRANTED,
                PermitNotificationReviewDecisionType.ACCEPTED.toDeterminationType());

        assertEquals(DeterminationType.REJECTED,
                PermitNotificationReviewDecisionType.REJECTED.toDeterminationType());

    }
}
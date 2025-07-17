package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestTaskTypeTest {

    @Test
    void getPeerReviewTypes() {
        Set<RequestTaskType> expectedRequestTaskTypes = Set.of(
            RequestTaskType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_SURRENDER_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_REVOCATION_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_VARIATION_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW,
            RequestTaskType.PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW,
            RequestTaskType.NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW,
            RequestTaskType.DRE_APPLICATION_PEER_REVIEW,
            RequestTaskType.NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW,
            RequestTaskType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW,
            RequestTaskType.NER_APPLICATION_PEER_REVIEW,
            RequestTaskType.DOAL_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW,
            RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
            RequestTaskType.RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW,
            RequestTaskType.AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW,
            RequestTaskType.AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW,
            RequestTaskType.AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW,
            RequestTaskType.AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW,
            RequestTaskType.EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW
            );

        expectedRequestTaskTypes.forEach(requestTaskType -> assertTrue(requestTaskType.isPeerReview()));
    }
}
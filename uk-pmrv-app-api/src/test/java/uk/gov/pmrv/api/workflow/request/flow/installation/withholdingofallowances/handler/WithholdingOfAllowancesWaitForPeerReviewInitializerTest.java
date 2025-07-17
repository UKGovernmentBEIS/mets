package uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.handler;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.withholdingofallowances.domain.WithholdingOfAllowancesRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WithholdingOfAllowancesWaitForPeerReviewInitializerTest {

    @Test
    void testInitializePayload() {
        Request request = new Request();
        WithholdingOfAllowancesRequestPayload requestPayload = new WithholdingOfAllowancesRequestPayload();
        request.setPayload(requestPayload);

        WithholdingOfAllowancesWaitForPeerReviewInitializer initializer = new WithholdingOfAllowancesWaitForPeerReviewInitializer();

        RequestTaskPayload payload = initializer.initializePayload(request);

        assertNotNull(payload);
        assertEquals(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW_PAYLOAD, payload.getPayloadType());
        assertEquals(requestPayload.getWithholdingOfAllowances(), ((WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getWithholdingOfAllowances());
        assertEquals(requestPayload.getWithholdingOfAllowancesSectionsCompleted(), ((WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getSectionsCompleted());
    }

    @Test
    void testGetRequestTaskTypes() {
        WithholdingOfAllowancesWaitForPeerReviewInitializer initializer = new WithholdingOfAllowancesWaitForPeerReviewInitializer();

        Set<RequestTaskType> taskTypes = initializer.getRequestTaskTypes();

        assertNotNull(taskTypes);
        assertEquals(1, taskTypes.size());
        assertTrue(taskTypes.contains(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW));
    }
}

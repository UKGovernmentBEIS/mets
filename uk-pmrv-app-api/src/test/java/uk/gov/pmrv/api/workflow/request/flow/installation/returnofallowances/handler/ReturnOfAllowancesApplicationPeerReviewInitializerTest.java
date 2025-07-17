package uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.handler;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.returnofallowances.domain.ReturnOfAllowancesRequestPayload;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReturnOfAllowancesApplicationPeerReviewInitializerTest {

    @Test
    void initializePayload() {
        Request request = new Request();
        ReturnOfAllowancesRequestPayload requestPayload = new ReturnOfAllowancesRequestPayload();
        request.setPayload(requestPayload);
        ReturnOfAllowancesApplicationPeerReviewInitializer initializer = new ReturnOfAllowancesApplicationPeerReviewInitializer();

        RequestTaskPayload payload = initializer.initializePayload(request);

        assertNotNull(payload);
        assertEquals(RequestTaskPayloadType.RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD, payload.getPayloadType());
        assertEquals(requestPayload.getReturnOfAllowances(), ((ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getReturnOfAllowances());
        assertEquals(requestPayload.getReturnOfAllowancesSectionsCompleted(), ((ReturnOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getSectionsCompleted());
    }

    @Test
    void getRequestTaskTypes() {
        ReturnOfAllowancesApplicationPeerReviewInitializer initializer = new ReturnOfAllowancesApplicationPeerReviewInitializer();

        Set<RequestTaskType> taskTypes = initializer.getRequestTaskTypes();

        assertNotNull(taskTypes);
        assertEquals(1, taskTypes.size());
        assertTrue(taskTypes.contains(RequestTaskType.RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW));
    }
}

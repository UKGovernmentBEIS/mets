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

public class WithholdingOfAllowancesApplicationPeerReviewInitializerTest {

    @Test
    public void initializePayload() {
        Request request = new Request();
        WithholdingOfAllowancesRequestPayload requestPayload = new WithholdingOfAllowancesRequestPayload();
        request.setPayload(requestPayload);
        WithholdingOfAllowancesApplicationPeerReviewInitializer initializer = new WithholdingOfAllowancesApplicationPeerReviewInitializer();

        RequestTaskPayload payload = initializer.initializePayload(request);

        assertNotNull(payload);
        assertEquals(RequestTaskPayloadType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW_PAYLOAD, payload.getPayloadType());
        assertEquals(requestPayload.getWithholdingOfAllowances(), ((WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getWithholdingOfAllowances());
        assertEquals(requestPayload.getWithholdingOfAllowancesSectionsCompleted(), ((WithholdingOfAllowancesApplicationPeerReviewRequestTaskPayload) payload).getSectionsCompleted());
    }

    @Test
    public void getRequestTaskTypes() {
        WithholdingOfAllowancesApplicationPeerReviewInitializer initializer = new WithholdingOfAllowancesApplicationPeerReviewInitializer();

        Set<RequestTaskType> taskTypes = initializer.getRequestTaskTypes();

        assertNotNull(taskTypes);
        assertEquals(1, taskTypes.size());
        assertTrue(taskTypes.contains(RequestTaskType.WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW));
    }
}

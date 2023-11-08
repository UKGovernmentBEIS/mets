package uk.gov.pmrv.api.workflow.request.flow.installation.doal.handler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.Doal;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.doal.domain.DoalRequestPayload;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class DoalApplicationPeerReviewInitializerRequestTaskHandlerTest {

    @InjectMocks
    private DoalApplicationPeerReviewInitializerRequestTaskHandler initializer;

    @Test
    void initializePayload() {
        final Request request = Request.builder()
                .type(RequestType.DOAL)
                .payload(DoalRequestPayload.builder()
                        .doal(Doal.builder().build())
                        .build())
                .build();

        // Invoke
        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        // Verify
        assertEquals(requestTaskPayload, DoalApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.DOAL_APPLICATION_PEER_REVIEW_PAYLOAD)
                .doal(Doal.builder().build())
                .build());
    }

    @Test
    void getRequestTaskTypes() {
        assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.DOAL_APPLICATION_PEER_REVIEW);
    }
}
